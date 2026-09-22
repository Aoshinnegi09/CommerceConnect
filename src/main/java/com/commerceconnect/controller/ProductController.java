package com.commerceconnect.controller;

import com.commerceconnect.dto.CategoryRequest;
import com.commerceconnect.dto.ProductRequest;
import com.commerceconnect.entity.Category;
import com.commerceconnect.entity.Product;
import com.commerceconnect.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable,
                                                      @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(productService.getProducts(pageable, categoryId));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(productService.createCategory(request));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
}
