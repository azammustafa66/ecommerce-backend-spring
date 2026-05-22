package com.spring.ecommerce.controller;

import com.spring.ecommerce.dto.CategoryRequest;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.services.CategoryServiceInterface;
import com.spring.ecommerce.utils.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryServiceInterface categoryService;

    public record CategoryUpdateRequest(
            @NotBlank(message = "Category name cannot be empty")
            @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
            String name
    ) {}

    public CategoryController(CategoryServiceInterface categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(200, categories, "Categories fetched successfully"));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category();
        category.setCategoryName(request.categoryName());

        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, createdCategory, "Category created successfully"));
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
            @Valid @RequestBody CategoryUpdateRequest request) {
        categoryService.updateCategory(oldName, request.name());
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Category updated successfully"));
    }
}
