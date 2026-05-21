package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.CategoryRequest;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.services.CategoryService;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    public record CategoryUpdateRequest(String name) {}

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/public/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category();
        category.setCategoryName(request.categoryName());

        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, category, "Category created successfully"));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                null,
                "Category deleted successfully"
        ));
    }

    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable long categoryId, @RequestBody CategoryUpdateRequest body) {
        categoryService.updateCategory(categoryId, body.name());
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Category updated successfully"));
    }
}
