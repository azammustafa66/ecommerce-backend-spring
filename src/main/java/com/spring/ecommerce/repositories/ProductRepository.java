package com.spring.ecommerce.repositories;

import com.spring.ecommerce.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findProductByProductNameIgnoreCase(String productName);
    Optional<Product> findProductByProductNameAndCategory_CategoryName(String productName, String categoryName);
}
