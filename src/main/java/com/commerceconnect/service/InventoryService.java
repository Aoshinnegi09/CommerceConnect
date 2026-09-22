package com.commerceconnect.service;

import com.commerceconnect.dto.InventoryRequest;
import com.commerceconnect.dto.InventoryResponse;
import com.commerceconnect.entity.Inventory;
import com.commerceconnect.entity.Product;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.InventoryRepository;
import com.commerceconnect.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final AuditService auditService;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            AuditService auditService) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.auditService = auditService;
    }

    @Transactional
    public InventoryResponse upsertInventory(InventoryRequest request, String actorEmail) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));

        Inventory inventory = inventoryRepository.findByProductId(request.productId()).orElse(new Inventory());
        inventory.setProduct(product);
        inventory.setQuantity(request.quantity());
        inventory.setReorderLevel(request.reorderLevel());
        inventory.setWarehouseLocation(request.warehouseLocation());

        Inventory saved = inventoryRepository.save(inventory);
        auditService.log("Inventory", saved.getId(), "UPSERT", actorEmail, "Updated stock for product " + product.getSku());
        return DtoMapper.toInventoryResponse(saved);
    }

    public InventoryResponse getByProduct(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        return DtoMapper.toInventoryResponse(inventory);
    }

    @Transactional
    public void deleteByProduct(Long productId, String actorEmail) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        Long inventoryId = inventory.getId();
        inventoryRepository.delete(inventory);
        auditService.log("Inventory", inventoryId, "DELETE", actorEmail, "Deleted inventory for product " + productId);
    }
}
