package com.luis.spring.security.msauth_poc.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    public void blacklist(String jti, Instant expiration) {
        long ttl = Duration.between(Instant.now(), expiration).getSeconds();
        if (ttl > 0) {
            String key = BLACKLIST_PREFIX + jti;
            stringRedisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(ttl));
            log.info("Token blacklisted in Redis with JTI: {} (TTL: {}s)", jti, ttl);
        }
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) return false;
        return stringRedisTemplate.hasKey(BLACKLIST_PREFIX + jti);
    }
}
