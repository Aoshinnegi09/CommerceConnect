package com.commerceconnect.service;

import com.commerceconnect.dto.CategoryRequest;
import com.commerceconnect.dto.CategoryResponse;
import com.commerceconnect.dto.ProductRequest;
import com.commerceconnect.dto.ProductResponse;
import com.commerceconnect.entity.Category;
import com.commerceconnect.entity.Product;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CategoryRepository;
import com.commerceconnect.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, AuditService auditService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, String actorEmail) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        Category saved = categoryRepository.save(category);
        auditService.log("Category", saved.getId(), "CREATE", actorEmail, "Created category " + saved.getName());
        return DtoMapper.toCategoryResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, String actorEmail) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.name());
        category.setDescription(request.description());
        Category saved = categoryRepository.save(category);
        auditService.log("Category", saved.getId(), "UPDATE", actorEmail, "Updated category " + saved.getName());
        return DtoMapper.toCategoryResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long id, String actorEmail) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        if (productRepository.countByCategoryId(id) > 0) {
            throw new IllegalArgumentException("Cannot delete category with associated products.");
        }
        categoryRepository.delete(category);
        auditService.log("Category", id, "DELETE", actorEmail, "Deleted category " + category.getName());
    }

    public Page<ProductResponse> getProducts(Pageable pageable,
                                             Long categoryId,
                                             String search,
                                             BigDecimal minPrice,
                                             BigDecimal maxPrice,
                                             Boolean active) {
        Specification<Product> specification = (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(builder.equal(root.get("active"), active == null || active));

            if (categoryId != null) {
                predicates.add(builder.equal(root.get("category").get("id"), categoryId));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("description")), pattern),
                        builder.like(builder.lower(root.get("sku")), pattern)
                ));
            }
            if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(specification, pageable).map(DtoMapper::toProductResponse);
    }

    public ProductResponse getProductById(Long id) {
        return DtoMapper.toProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, String actorEmail) {
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
        Product saved = productRepository.save(product);
        auditService.log("Product", saved.getId(), "CREATE", actorEmail, "Created product " + saved.getSku());
        return DtoMapper.toProductResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, String actorEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Product existingBySku = productRepository.findBySku(request.sku());
        if (existingBySku != null && !existingBySku.getId().equals(id)) {
            throw new IllegalArgumentException("Product with SKU already exists.");
        }

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setActive(request.active());
        Product saved = productRepository.save(product);
        auditService.log("Product", saved.getId(), "UPDATE", actorEmail, "Updated product " + saved.getSku());
        return DtoMapper.toProductResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id, String actorEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
        auditService.log("Product", id, "DEACTIVATE", actorEmail, "Soft deleted product " + product.getSku());
    }
}
