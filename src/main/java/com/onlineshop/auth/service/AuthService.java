package com.onlineshop.auth.service;

import com.onlineshop.auth.dto.LoginRequest;
import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.dto.RegisterRequest;
import com.onlineshop.auth.dto.ChangePasswordRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    void changePassword(String username, ChangePasswordRequest request);
    AuthResponse oauth2Login(String email, String name);
}
