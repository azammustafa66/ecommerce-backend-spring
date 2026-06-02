package com.spring.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(@NotBlank @NotNull String refreshToken) {}
