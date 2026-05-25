package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.ProductDTO;
import com.spring.ecommerce.dto.ProductResponse;
import com.spring.ecommerce.dto.UpdateProductDTO;
import com.spring.ecommerce.models.Category;
import com.spring.ecommerce.models.Product;
import com.spring.ecommerce.repositories.CategoryRepository;
import com.spring.ecommerce.repositories.ProductRepository;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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

    public ProductDTO addProduct(ProductDTO product, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
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

    public ProductDTO updateProduct(UpdateProductDTO product, UUID productId) {
        Product existingProduct = productRepository.findById(productId)
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

        if (product.getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist"));
            existingProduct.setCategory(category);
        }

        Product savedProduct = productRepository.save(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));
        productRepository.delete(product);
    }

    public ProductDTO updateProductImage(UUID productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));

        String extension = getString(image);
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String fileName = product.getId() + "-" + timestamp + extension;
        Path imageDirectory = Path.of("images");
        Path imagePath = imageDirectory.resolve(fileName);

        Files.createDirectories(imageDirectory);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        product.setProductImageUrl(fileName);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    private static @NonNull String getString(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product image is required");
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank() || !originalFilename.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product image file name is invalid");
        }

        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
