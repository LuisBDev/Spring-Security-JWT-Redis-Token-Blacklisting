package com.luis.spring.security.msauth_poc.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

/**
 * Rate limiting filter for the login endpoint using Redis.
 * Limits requests per IP address using Redis INCR/EXPIRE pattern.
 * Configuration: 10 attempts per minute per IP.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:login:";

    @Value("${app.rate-limit.max-attempts:10}")
    private Integer MAX_ATTEMPTS;


    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod())
                && "/api/auth/login".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + clientIp;

        try {
            Long currentCount = stringRedisTemplate.opsForValue().increment(key);

            if (currentCount != null && currentCount == 1) {
                stringRedisTemplate.expire(key, WINDOW_DURATION);
            }

            if (currentCount != null && currentCount > MAX_ATTEMPTS) {
                log.warn("Rate limit exceeded for IP: {} ({} attempts)", clientIp, currentCount);

                Long ttl = stringRedisTemplate.getExpire(key);
                long retryAfter = (ttl != null && ttl > 0) ? ttl : WINDOW_DURATION.getSeconds();

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too many login attempts. Please try again later.");
                problemDetail.setTitle("Rate Limit Exceeded");
                problemDetail.setType(URI.create("about:blank"));
                problemDetail.setProperty("retry_after_seconds", retryAfter);

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.setHeader("Retry-After", String.valueOf(retryAfter));
                objectMapper.writeValue(response.getOutputStream(), problemDetail);
                return;
            }
        } catch (Exception e) {
            // If Redis is unavailable, allow the request through (fail-open)
            log.error("Rate limiting failed (Redis unavailable): {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
