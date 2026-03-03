package com.luis.spring.security.msauth_poc.service;

import com.luis.spring.security.msauth_poc.dto.request.LoginRequest;
import com.luis.spring.security.msauth_poc.dto.request.RefreshTokenRequest;
import com.luis.spring.security.msauth_poc.dto.request.RegisterRequest;
import com.luis.spring.security.msauth_poc.dto.response.AuthResponse;

/**
 * @author Luis Balarezo
 **/
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken, String refreshToken);

}
