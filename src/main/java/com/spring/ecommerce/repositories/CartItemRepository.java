package com.spring.ecommerce.repositories;

import com.spring.ecommerce.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByCart_CartIdAndProduct_Id(UUID cartId, UUID productId);
    List<CartItems> findByProduct_Id(UUID productId);
}
