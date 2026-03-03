package com.luis.spring.security.msauth_poc.controller;

import com.luis.spring.security.msauth_poc.dto.request.LoginRequest;
import com.luis.spring.security.msauth_poc.dto.request.RefreshTokenRequest;
import com.luis.spring.security.msauth_poc.dto.request.RegisterRequest;
import com.luis.spring.security.msauth_poc.dto.response.AuthResponse;
import com.luis.spring.security.msauth_poc.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Luis Balarezo
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) RefreshTokenRequest refreshRequest) {
        String accessToken = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            accessToken = authHeader.substring(BEARER_PREFIX.length());
        }

        String refreshToken = (refreshRequest != null) ? refreshRequest.getRefreshToken() : null;

        authService.logout(accessToken, refreshToken);
        return ResponseEntity.noContent().build();
    }


}
