package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.ProductDTO;
import com.spring.ecommerce.dto.ProductResponse;
import com.spring.ecommerce.dto.UpdateProductDTO;
import com.spring.ecommerce.services.ProductService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiResponse<ProductResponse>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), productService.getProducts(page, size, sortBy, direction), "Fetched all products successfully"));
    }

    @PostMapping(value = "/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(
            @Valid @RequestPart("product") ProductDTO product,
            @PathVariable UUID categoryId,
            @RequestPart(value = "productImage", required = false) MultipartFile productImage) throws IOException {
        ProductDTO addedProduct = productService.addProduct(product, categoryId, productImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), addedProduct, "Product added successfully"));
    }

    @PatchMapping("/update/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@Valid @RequestBody UpdateProductDTO product, @PathVariable UUID productId) {
        ProductDTO updatedProduct = productService.updateProduct(product, productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), updatedProduct, "Product updated successfully"));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), null, "Product deleted successfully"));
    }

    @PutMapping("/{productId}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductImage(@PathVariable UUID productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), updatedProduct, "Product image updated successfully"));
    }
}
