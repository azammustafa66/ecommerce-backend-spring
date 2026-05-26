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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private static final String DEFAULT_PRODUCT_IMAGE_URL = "https://placehold.co/600x400?text=Product";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productName",
            "price",
            "specialPrice",
            "productStock",
            "createdAt",
            "updatedAt"
    );

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileUploadService fileUploadService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileUploadService fileUploadService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileUploadService = fileUploadService;
    }

    public ProductDTO addProduct(ProductDTO product, UUID categoryId, MultipartFile productImage) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist"));

        if (productRepository.existsByProductNameIgnoreCaseAndCategory_CategoryId(product.getProductName(), categoryId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product already exists in this category");
        }

        Product newProduct = modelMapper.map(product, Product.class);
        newProduct.setCategory(category);
        if (newProduct.getProductImageUrl() == null || newProduct.getProductImageUrl().isBlank()) {
            newProduct.setProductImageUrl(DEFAULT_PRODUCT_IMAGE_URL);
        }

        Product savedProduct = productRepository.save(newProduct);
        if (productImage != null && !productImage.isEmpty()) {
            String imageUrl = fileUploadService.upload("product", savedProduct.getId(), productImage);
            savedProduct.setProductImageUrl(imageUrl);
            savedProduct = productRepository.save(savedProduct);
        }

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public ProductResponse getProducts(Integer page, Integer size, String sortBy, String direction) {
        String validatedSortBy = validateSortBy(sortBy);
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(validatedSortBy).ascending()
                : Sort.by(validatedSortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();
        return ProductResponse.builder().products(products).page(productPage.getNumber()).size(productPage.getSize()).totalElements(productPage.getTotalElements()).totalPages(productPage.getTotalPages()).lastPage(productPage.isLast()).build();

    }

    private String validateSortBy(String sortBy) {
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            return "createdAt";
        }

        return sortBy;
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

        String fileName = fileUploadService.upload("product", product.getId(), image);
        product.setProductImageUrl(fileName);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
}
