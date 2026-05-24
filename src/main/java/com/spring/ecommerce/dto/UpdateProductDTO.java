package com.spring.ecommerce.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDTO {
    private String productName;
    private String description;

    @Positive(message = "Product price must be greater than zero")
    private Double price;

    @PositiveOrZero(message = "Product stock cannot be negative")
    private Integer productStock;

    @PositiveOrZero(message = "Special price cannot be negative")
    private Double specialPrice;

    private String categoryName;
}
