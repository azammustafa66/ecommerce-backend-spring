package com.spring.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private UUID id;

    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    private String imageURL;

    @NotBlank(message = "Product description cannot be empty")
    private String description;

    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be greater than zero")
    private Double price;

    @NotNull(message = "Product stock is required")
    @PositiveOrZero(message = "Product stock cannot be negative")
    private Integer productStock;

    @PositiveOrZero(message = "Special price cannot be negative")
    private Double specialPrice;

    private CategoryDTO category;
}
