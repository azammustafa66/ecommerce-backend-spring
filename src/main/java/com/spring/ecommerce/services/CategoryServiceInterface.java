package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.CategoryDTO;
import com.spring.ecommerce.dto.CategoryResponse;

import java.util.List;

public interface CategoryServiceInterface {
    CategoryResponse getAllCategories(Integer page, Integer size);
    CategoryDTO createCategory(String categoryName);
    void deleteCategory(String name);
    void updateCategory(String oldName, String newName);
}
