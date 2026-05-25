package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.CategoryDTO;
import com.spring.ecommerce.dto.CategoryResponse;
import com.spring.ecommerce.services.CategoryService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> getAllCategories(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        CategoryResponse categories = categoryService.getAllCategories(page, size);

        return ResponseEntity.ok(new ApiResponse<>(200, categories, "Categories fetched successfully"));
    }

    @GetMapping("/public/categories/search")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> searchCategories(@RequestParam(name = "name") String name) {
        List<CategoryDTO> categories = categoryService.searchCategories(name);

        return ResponseEntity.ok(new ApiResponse<>(200, categories, "Categories fetched successfully"));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO body) {
        CategoryDTO response = categoryService.createCategory(body.getCategoryName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), response, "Category created successfully"));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                null,
                "Category deleted successfully"
        ));
    }

    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<String>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryDTO body) {
        categoryService.updateCategory(categoryId, body.getCategoryName());
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Category updated successfully"));
    }
}
