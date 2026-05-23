package com.spring.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private UUID categoryId;

    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    private String categoryName;
}
