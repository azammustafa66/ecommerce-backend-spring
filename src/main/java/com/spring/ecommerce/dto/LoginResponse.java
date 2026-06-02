package com.spring.ecommerce.dto;

public record LoginResponse(String
                            tokenType, String accessToken, String refreshToken) {}
