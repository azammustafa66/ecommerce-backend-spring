package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.CategoryDTO;
import com.spring.ecommerce.dto.CategoryResponse;
import com.spring.ecommerce.services.CategoryServiceInterface;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryServiceInterface categoryService;

    public CategoryController(CategoryServiceInterface categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> getAllCategories() {
        CategoryResponse categories = categoryService.getAllCategories();

        return ResponseEntity.ok(new ApiResponse<>(200, categories, "Categories fetched successfully"));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO body) {
        CategoryDTO response = categoryService.createCategory(body.getCategoryName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, response, "Category created successfully"));
    }

    @DeleteMapping("/admin/categories/{name}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String name) {
        categoryService.deleteCategory(name);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                null,
                "Category deleted successfully"
        ));
    }

    @PatchMapping("/admin/categories/{oldName}")
    public ResponseEntity<ApiResponse<String>> updateCategory(
            @PathVariable String oldName,
            @Valid @RequestBody CategoryDTO body) {
        categoryService.updateCategory(oldName, body.getCategoryName());
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Category updated successfully"));
    }
}
