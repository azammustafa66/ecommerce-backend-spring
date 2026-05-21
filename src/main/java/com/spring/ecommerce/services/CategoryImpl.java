package com.spring.ecommerce.services;

import com.spring.ecommerce.models.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryImpl implements CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        boolean doesExist = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase(category.getCategoryName()));
        if (doesExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists");
        } else {
            category.setCategoryId(nextId++);
            categories.add(category);
        }
    }

    @Override
    public void deleteCategory(long categoryId) {
        Category category = categories.stream().filter(c -> c.getCategoryId() == categoryId).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        categories.remove(category);
    }

    @Override
    public void updateCategory(long categoryId, String name) {
        Category category = categories.stream().filter(c -> c.getCategoryId() == categoryId).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category.setCategoryName(name);
    }
}
