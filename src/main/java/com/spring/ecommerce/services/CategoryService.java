package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.CategoryDTO;
import com.spring.ecommerce.dto.CategoryResponse;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategories(categoryDTOS);

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(String name) {
        String categoryName = normalizeCategoryName(name);

        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists");
        }

        Category category = new Category();
        category.setCategoryName(categoryName);
        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(String name) {
        Category category = categoryRepository.findByCategoryNameIgnoreCase(name)
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
