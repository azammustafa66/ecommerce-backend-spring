package com.spring.ecommerce.services;

import com.spring.ecommerce.models.Category;

import java.util.List;

public interface CategoryServiceInterface {
    List<Category> getAllCategories();
    Category createCategory(Category category);
    void deleteCategory(String name);
    void updateCategory(String oldName, String newName);
}
