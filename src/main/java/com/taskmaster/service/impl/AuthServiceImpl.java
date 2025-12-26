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
    private final AuthMapper authMapper;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        UserRole role = resolveRole(request.getRole());

        UserEntity user = authMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        UserEntity savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String email = jwtTokenProvider.extractUsername(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (!jwtTokenProvider.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        return buildAuthResponse(user);
    }

    @Override
    public void logout(String token) {
        log.info("Token successfully blacklisted");
    }

    private AuthResponse buildAuthResponse(UserEntity user) {

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(accessToken, refreshToken, jwtExpiration, userResponse);
    }

    private UserRole resolveRole(String roleStr) {

        if (roleStr == null || roleStr.isBlank()) {
            return UserRole.ROLE_USER;
        }

        String normalized = roleStr.startsWith("ROLE_")
                ? roleStr.toUpperCase()
                : "ROLE_" + roleStr.toUpperCase();

        try {
            return UserRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return UserRole.ROLE_USER;
        }
    }
}