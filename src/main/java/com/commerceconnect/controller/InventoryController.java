package com.commerceconnect.controller;

import com.commerceconnect.dto.InventoryRequest;
import com.commerceconnect.dto.InventoryResponse;
import com.commerceconnect.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<InventoryResponse> upsert(@Valid @RequestBody InventoryRequest request, Authentication authentication) {
        return ResponseEntity.ok(inventoryService.upsertInventory(request, authentication.getName()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> get(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProduct(productId));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long productId, Authentication authentication) {
        inventoryService.deleteByProduct(productId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
