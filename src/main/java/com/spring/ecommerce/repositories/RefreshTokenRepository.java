package com.spring.ecommerce.repositories;

import com.spring.ecommerce.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String token);
    void deleteAllByUser_Id(UUID userId);
}
