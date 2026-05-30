package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.CategoryDTO;
import com.spring.ecommerce.dto.CategoryResponse;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryResponse getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();

        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        return CategoryResponse.builder()
                .categories(categoryDTOS)
                .page(categoryPage.getNumber())
                .size(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .lastPage(categoryPage.isLast())
                .totalElements(categoryPage.getTotalElements())
                .build();
    }

    public List<CategoryDTO> searchCategories(String name) {
        String categoryName = normalizeCategoryName(name);

        List<Category> categories =
                categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName);

        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found");
        }

        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
    }

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

    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryRepository.delete(category);
    }

    public void updateCategory(UUID categoryId, String newName) {
        String updatedName = normalizeCategoryName(newName);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!category.getCategoryName().equalsIgnoreCase(updatedName) && categoryRepository.existsByCategoryNameIgnoreCase(updatedName)) {
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
