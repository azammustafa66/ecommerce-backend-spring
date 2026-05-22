package com.spring.ecommerce.repositories;

import com.spring.ecommerce.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
}
