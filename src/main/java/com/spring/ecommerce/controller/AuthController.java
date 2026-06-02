package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.LoginRequest;
import com.spring.ecommerce.dto.LoginResponse;
import com.spring.ecommerce.dto.RefreshTokenRequest;
import com.spring.ecommerce.dto.RegisterRequest;
import com.spring.ecommerce.services.AuthService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody RegisterRequest req) {
        authService.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), null, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse data = authService.loginUser(req);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        LoginResponse data = authService.refreshToken(req);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Refresh successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest req) {
        authService.logoutUser(req);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), null, "Logout successful"));
    }
}