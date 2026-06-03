package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.AddToCartRequest;
import com.spring.ecommerce.dto.CartResponseDTO;
import com.spring.ecommerce.services.CartService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PutMapping("")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponseDTO data = cartService.addToCart(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Item added to cart"));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CartResponseDTO>>> getAllCarts() {
        List<CartResponseDTO> data = cartService.getAllCart();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "All carts fetched successfully"));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart() {
        CartResponseDTO data = cartService.getCurrentUserCart();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Cart fetched successfully."));
    }

    @PatchMapping("")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponseDTO data = cartService.updateCart(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Cart updated successfully"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> deleteItem(@PathVariable UUID productId) {
        CartResponseDTO data = cartService.deleteItem(productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Item removed from cart"));
    }

    @DeleteMapping("")
    public ResponseEntity<ApiResponse<CartResponseDTO>> clearCart() {
        CartResponseDTO data = cartService.clearCart();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, "Cart cleared"));
    }
}

