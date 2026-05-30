package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.RegisterRequest;
import com.spring.ecommerce.services.AuthService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody RegisterRequest req) {
        authService.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, null, "User registered successfully"));
    }
}
