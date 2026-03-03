package com.luis.spring.security.msauth_poc.service;

import com.luis.spring.security.msauth_poc.config.JwtProperties;
import com.luis.spring.security.msauth_poc.dto.request.LoginRequest;
import com.luis.spring.security.msauth_poc.dto.request.RefreshTokenRequest;
import com.luis.spring.security.msauth_poc.dto.request.RegisterRequest;
import com.luis.spring.security.msauth_poc.dto.response.AuthResponse;
import com.luis.spring.security.msauth_poc.entity.Role;
import com.luis.spring.security.msauth_poc.entity.User;
import com.luis.spring.security.msauth_poc.exception.EmailAlreadyExistsException;
import com.luis.spring.security.msauth_poc.exception.ResourceNotFoundException;
import com.luis.spring.security.msauth_poc.exception.TokenException;
import com.luis.spring.security.msauth_poc.repository.RoleRepository;
import com.luis.spring.security.msauth_poc.repository.UserRepository;
import com.luis.spring.security.msauth_poc.security.CustomUserDetails;
import com.luis.spring.security.msauth_poc.security.JwtService;
import com.luis.spring.security.msauth_poc.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Luis Balarezo
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return buildAuthResponse(userDetails);


    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("User logged in successfully: {}", userDetails.getUsername());

        return buildAuthResponse(userDetails);

    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new TokenException("Invalid token type: expected refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new TokenException("Invalid or expired refresh token");
        }

        // Blacklist the old refresh token (rotation)
        String oldJti = jwtService.extractJti(refreshToken);
        tokenBlacklistService.blacklist(oldJti, jwtService.extractExpiration(refreshToken).toInstant());
        log.info("Refresh token rotated for user: {}", username);

        return buildAuthResponse(userDetails);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            try {
                String accessJti = jwtService.extractJti(accessToken);
                tokenBlacklistService.blacklist(accessJti, jwtService.extractExpiration(accessToken).toInstant());
            } catch (Exception e) {
                log.warn("Failed to blacklist access token: {}", e.getMessage());
            }
        }

        if (refreshToken != null) {
            try {
                String refreshJti = jwtService.extractJti(refreshToken);
                tokenBlacklistService.blacklist(refreshJti, jwtService.extractExpiration(refreshToken).toInstant());
            } catch (Exception e) {
                log.warn("Failed to blacklist refresh token: {}", e.getMessage());
            }
        }

        log.info("User logged out, tokens blacklisted");
    }

    private AuthResponse buildAuthResponse(CustomUserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

}
