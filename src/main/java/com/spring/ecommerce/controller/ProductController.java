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

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> getProducts(@RequestParam(name = "page", defaultValue = "0") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), productService.getProducts(page, size), "Fetched all products successfully"));
    }

    @PostMapping("/{categoryName}")
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO product, @PathVariable String categoryName) {
        ProductDTO addedProduct = productService.addProduct(product, categoryName);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), addedProduct, "Product added successfully"));
    }

    @PatchMapping("/update/{productName}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@Valid @RequestBody UpdateProductDTO product, @PathVariable String productName) {
        ProductDTO updatedProduct = productService.updateProduct(product, productName);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), updatedProduct, "Product updated successfully"));
    }

    @DeleteMapping("/{categoryName}/{productName}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable String categoryName, @PathVariable String productName) {
        productService.deleteProduct(productName, categoryName);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), null, "Product deleted successfully"));
    }
}
