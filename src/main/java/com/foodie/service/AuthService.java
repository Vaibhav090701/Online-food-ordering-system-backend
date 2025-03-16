package com.foodie.service;

import com.foodie.dto.AuthResponse;
import com.foodie.request.LoginRequest;
import com.foodie.request.PasswordResetRequest;
import com.foodie.request.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
    void resetPassword(PasswordResetRequest request);
    AuthResponse refreshToken(String refreshToken);

}
