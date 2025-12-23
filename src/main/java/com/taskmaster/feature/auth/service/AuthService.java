package com.taskmaster.feature.auth.service;

import com.taskmaster.feature.auth.dto.request.LoginRequest;
import com.taskmaster.feature.auth.dto.request.RegisterRequest;
import com.taskmaster.feature.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String token);
    void logout(String token);
}