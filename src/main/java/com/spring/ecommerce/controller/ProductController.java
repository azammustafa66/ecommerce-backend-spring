package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.ProductDTO;
import com.spring.ecommerce.dto.ProductResponse;
import com.spring.ecommerce.dto.UpdateProductDTO;
import com.spring.ecommerce.services.ProductService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<ProductResponse>> getProducts(@RequestParam(name = "page", defaultValue = "0") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), productService.getProducts(page, size), "Fetched all products successfully"));
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO product, @PathVariable UUID categoryId) {
        ProductDTO addedProduct = productService.addProduct(product, categoryId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), addedProduct, "Product added successfully"));
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@Valid @RequestBody UpdateProductDTO product, @PathVariable UUID productId) {
        ProductDTO updatedProduct = productService.updateProduct(product, productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), updatedProduct, "Product updated successfully"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), null, "Product deleted successfully"));
    }

    @PutMapping("/{productId}/image")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductImage(@PathVariable UUID productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), updatedProduct, "Product image updated successfully"));
    }
}
