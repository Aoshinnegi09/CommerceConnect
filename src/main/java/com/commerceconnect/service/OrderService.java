package com.commerceconnect.service;

import com.commerceconnect.dto.OrderRequest;
import com.commerceconnect.entity.*;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        NotificationRepository notificationRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must include at least one item.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.productId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + product.getName()));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is inactive: " + product.getName());
            }
            if (inventory.getQuantity() < itemRequest.quantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(itemTotal);
            orderItems.add(item);

            inventory.setQuantity(inventory.getQuantity() - itemRequest.quantity());
            inventoryRepository.save(inventory);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("CARD");
        payment.setAmount(totalAmount);
        payment.setStatus("PAID");
        payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        paymentRepository.save(payment);

        order.setPaymentStatus("PAID");
        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(customer.getUser());
        notification.setTitle("Order placed successfully");
        notification.setMessage("Your order " + order.getOrderNumber() + " has been confirmed.");
        notification.setNotificationType("ORDER");
        notificationRepository.save(notification);

        return order;
    }
}
