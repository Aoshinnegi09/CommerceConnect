package com.commerceconnect.service;

import com.commerceconnect.dto.*;
import com.commerceconnect.entity.*;

import java.math.BigDecimal;
import java.util.List;

final class DtoMapper {

    private DtoMapper() {
    }

    static CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.isActive(),
                toCategoryResponse(product.getCategory())
        );
    }

    static InventoryResponse toInventoryResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getQuantity(),
                inventory.getReorderLevel(),
                inventory.getWarehouseLocation(),
                inventory.getVersion()
        );
    }

    static CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getUser().getEmail(),
                customer.getUser().getFirstName(),
                customer.getUser().getLastName(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getCountry()
        );
    }

    static PromotionResponse toPromotionResponse(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountType(),
                promotion.getDiscountValue(),
                promotion.getMaxDiscountAmount(),
                promotion.getValidFrom(),
                promotion.getValidTo(),
                promotion.isActive()
        );
    }

    static CampaignResponse toCampaignResponse(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getDescription(),
                campaign.getCampaignType(),
                campaign.getStatus(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getCreatedBy() == null ? null : campaign.getCreatedBy().getId()
        );
    }

    static NotificationResponse toNotificationResponse(NotificationEntity notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getNotificationType(),
                notification.getNotificationStatus(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    static OrderResponse toOrderResponse(Order order, List<OrderItem> items, BigDecimal discountAmount, Long promotionId) {
        List<OrderItemResponse> itemResponses = items.stream().map(item -> new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        )).toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getTotalAmount(),
                discountAmount,
                promotionId,
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
