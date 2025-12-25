package com.taskmaster.service.impl;

import com.taskmaster.common.enums.UserRole;
import com.taskmaster.common.exception.custom.BadRequestException;
import com.taskmaster.common.exception.custom.DuplicateResourceException;
import com.taskmaster.config.security.JwtTokenProvider;
import com.taskmaster.dto.request.LoginRequest;
import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.dto.response.AuthResponse;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.mapper.AuthMapper;
import com.taskmaster.mapper.UserMapper;
import com.taskmaster.repository.UserRepository;
import com.taskmaster.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final AuthMapper authMapper; // Inject AuthMapper

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            throw new BadRequestException("Passwords do not match");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate registration attempt for email: {}", request.getEmail());
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Normalize role
        String roleStr = request.getRole();
        String normalizedRole = (roleStr == null || roleStr.isBlank())
                ? "ROLE_USER"
                : (roleStr.startsWith("ROLE_") ? roleStr.toUpperCase() : "ROLE_" + roleStr.toUpperCase());

        UserRole role;
        try {
            role = UserRole.valueOf(normalizedRole);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid role '{}' for email {}, defaulting to ROLE_USER", roleStr, request.getEmail());
            role = UserRole.ROLE_USER;
        }

        // Map RegisterRequest → UserEntity (using AuthMapper)
        UserEntity userEntity = authMapper.toUserEntity(request);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(role);

        // Save entity
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);

        // Map entity → response
        UserResponse userResponse = userMapper.toResponse(savedUser);

        return AuthResponse.of(accessToken, refreshToken, jwtExpiration, userResponse);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("User authenticated successfully: {}", user.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(accessToken, refreshToken, jwtExpiration, userResponse);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Refreshing access token");

        String userEmail = jwtTokenProvider.extractUsername(refreshToken);
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (!jwtTokenProvider.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(newAccessToken, newRefreshToken, jwtExpiration, userResponse);
    }

    @Override
    public void logout(String token) {
        log.info("User logged out");
    }
}
