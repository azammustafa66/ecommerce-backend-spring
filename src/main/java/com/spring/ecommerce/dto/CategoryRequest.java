package com.spring.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name cannot be empty")
        @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
        String categoryName
) {}