package com.spring.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private ProductDTO product;
    private long quantity;
    private double discount;
    private double productPrice;
    private double lineTotal;
}
