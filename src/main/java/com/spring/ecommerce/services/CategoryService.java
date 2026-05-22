package com.spring.ecommerce.services;

import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        String categoryName = normalizeCategoryName(category.getCategoryName());

        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists");
        }

        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String name) {
        String categoryName = normalizeCategoryName(name);

        Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryRepository.delete(category);
    }

    @Override
    public void updateCategory(String oldName, String newName) {
        String existingName = normalizeCategoryName(oldName);
        String updatedName = normalizeCategoryName(newName);

        Category category = categoryRepository.findByCategoryNameIgnoreCase(existingName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!existingName.equalsIgnoreCase(updatedName) && categoryRepository.existsByCategoryNameIgnoreCase(updatedName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists");
        }

        category.setCategoryName(updatedName);
        categoryRepository.save(category);
    }

    private String normalizeCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name cannot be empty");
        }

        return categoryName.trim();
    }
}
