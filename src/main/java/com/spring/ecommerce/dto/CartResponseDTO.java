package com.spring.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
    private UUID cartId;
    private double totalPrice;
    @Builder.Default
    private List<CartItemDTO> items = new ArrayList<>();
}
