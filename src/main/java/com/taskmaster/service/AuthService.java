package com.taskmaster.service;

import com.taskmaster.dto.request.LoginRequest;
import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String token);
    void logout(String token);
}