package com.spring.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;
    private String productName;
    private String description;
    private Integer productStock;
    private Double price;
    private Double specialPrice;
    private String productImageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
