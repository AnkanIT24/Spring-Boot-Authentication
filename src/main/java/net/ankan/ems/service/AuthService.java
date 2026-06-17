package net.ankan.ems.service;

import net.ankan.ems.dto.AuthResponse;
import net.ankan.ems.dto.LoginRequest;
import net.ankan.ems.dto.RegisterRequest;
import net.ankan.ems.dto.ChangePasswordRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    void changePassword(ChangePasswordRequest request);
}