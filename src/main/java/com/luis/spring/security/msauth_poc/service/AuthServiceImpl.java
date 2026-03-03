package com.luis.spring.security.msauth_poc.service;

import com.luis.spring.security.msauth_poc.dto.request.LoginRequest;
import com.luis.spring.security.msauth_poc.dto.request.RefreshTokenRequest;
import com.luis.spring.security.msauth_poc.dto.request.RegisterRequest;
import com.luis.spring.security.msauth_poc.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Luis Balarezo
 **/
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    @Override
    public void logout(String accessToken, String refreshToken) {

    }

}
