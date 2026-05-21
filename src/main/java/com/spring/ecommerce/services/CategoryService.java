package com.spring.ecommerce.services;

import com.spring.ecommerce.models.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void createCategory(Category category);
    void deleteCategory(long categoryId);
    void updateCategory(long categoryId, String name);
}
