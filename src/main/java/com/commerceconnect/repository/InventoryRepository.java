package com.commerceconnect.repository;

import com.commerceconnect.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query("update Inventory i set i.quantity = i.quantity - :quantity where i.product.id = :productId and i.quantity >= :quantity")
    int decrementStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
