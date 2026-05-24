package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.ProductDTO;
import com.spring.ecommerce.dto.ProductResponse;
import com.spring.ecommerce.dto.UpdateProductDTO;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.models.Product;
import com.spring.ecommerce.repositories.CategoryRepository;
import com.spring.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public ProductDTO addProduct(ProductDTO product, String categoryName) {
        Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist"));

        Product newProduct = modelMapper.map(product, Product.class);
        newProduct.setCategory(category);

        Product savedProduct = productRepository.save(newProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public ProductResponse getProducts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();
        return ProductResponse.builder().products(products).page(page).size(size).totalElements(productPage.getTotalElements()).totalPages(productPage.getTotalPages()).lastPage(productPage.isLast()).build();

    }

    public ProductDTO updateProduct(UpdateProductDTO product, String productName) {
        Product existingProduct = productRepository.findProductByProductNameIgnoreCase(productName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));

        if (product.getProductName() != null) {
            existingProduct.setProductName(product.getProductName());
        }

        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }

        if (product.getProductStock() != null) {
            existingProduct.setProductStock(product.getProductStock());
        }

        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }

        if (product.getSpecialPrice() != null) {
            existingProduct.setSpecialPrice(product.getSpecialPrice());
        }

        if (product.getCategoryName() != null) {
            Category category = categoryRepository.findByCategoryNameIgnoreCase(product.getCategoryName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist"));
            existingProduct.setCategory(category);
        }

        Product savedProduct = productRepository.save(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public void deleteProduct(String productName, String categoryName) {
        Product product = productRepository.findProductByProductNameAndCategory_CategoryName(productName, categoryName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));
        productRepository.delete(product);
    }
}
