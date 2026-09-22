package com.commerceconnect.service;

import com.commerceconnect.dto.CategoryRequest;
import com.commerceconnect.dto.ProductRequest;
import com.commerceconnect.entity.Category;
import com.commerceconnect.entity.Product;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CategoryRepository;
import com.commerceconnect.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    public Page<Product> getProducts(Pageable pageable, Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        }
        return productRepository.findByActiveTrue(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        if (productRepository.findBySku(request.sku()) != null) {
            throw new IllegalArgumentException("Product with SKU already exists.");
        }

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));
        }

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setActive(request.active());
        return productRepository.save(product);
    }
}
