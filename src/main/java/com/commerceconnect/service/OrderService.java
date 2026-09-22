package com.commerceconnect.service;

import com.commerceconnect.dto.OrderRequest;
import com.commerceconnect.dto.OrderResponse;
import com.commerceconnect.entity.*;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    private final NotificationRepository notificationRepository;
    private final PromotionService promotionService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        OrderItemRepository orderItemRepository,
                        NotificationRepository notificationRepository,
                        PromotionService promotionService,
                        PaymentService paymentService,
                        UserRepository userRepository,
                        AuditService auditService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.notificationRepository = notificationRepository;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String actorEmail, String idempotencyKey) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        ensureCustomerOwnership(customer, actorEmail);

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must include at least one item.");
        }

        String effectiveIdempotencyKey = idempotencyKey == null || idempotencyKey.isBlank()
                ? "gen-" + UUID.randomUUID()
                : idempotencyKey;

        var existing = orderRepository.findByIdempotencyKeyAndCustomerId(effectiveIdempotencyKey, customer.getId());
        if (existing.isPresent()) {
            Order order = existing.get();
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return DtoMapper.toOrderResponse(order, items, order.getDiscountAmount(), order.getPromotion() == null ? null : order.getPromotion().getId());
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.productId()));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is inactive: " + product.getName());
            }

            int updatedRows = inventoryRepository.decrementStock(product.getId(), itemRequest.quantity());
            if (updatedRows == 0) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(itemTotal);
            orderItems.add(item);
        }

        Promotion promotion = null;
        BigDecimal discount = BigDecimal.ZERO;
        if (request.promotionId() != null) {
            promotion = promotionService.getPromotionEntityById(request.promotionId());
            discount = promotionService.calculateDiscount(subtotal, promotion);
        }

        BigDecimal totalAmount = subtotal.subtract(discount);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        order.setDiscountAmount(discount);
        order.setPromotion(promotion);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setIdempotencyKey(effectiveIdempotencyKey);
        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }

        Payment payment = paymentService.process(order, totalAmount, effectiveIdempotencyKey);
        order.setPaymentStatus(payment.getStatus());
        order.setStatus(payment.getStatus() == PaymentStatus.PAID ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(customer.getUser());
        notification.setTitle("Order placed successfully");
        notification.setMessage("Your order " + order.getOrderNumber() + " has status " + order.getStatus() + ".");
        notification.setNotificationType(NotificationType.ORDER);
        notification.setNotificationStatus(NotificationStatus.SENT);
        notificationRepository.save(notification);

        auditService.log("Order", order.getId(), "CREATE", actorEmail, "Created order " + order.getOrderNumber());

        List<OrderItem> savedItems = orderItemRepository.findByOrderId(order.getId());
        return DtoMapper.toOrderResponse(order, savedItems, discount, promotion == null ? null : promotion.getId());
    }

    public OrderResponse getOrderById(Long id, String actorEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        ensureCustomerOwnership(order.getCustomer(), actorEmail);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return DtoMapper.toOrderResponse(order, items, order.getDiscountAmount(), order.getPromotion() == null ? null : order.getPromotion().getId());
    }

    public Page<OrderResponse> getOrdersForCustomer(Long customerId, Pageable pageable, String actorEmail) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        ensureCustomerOwnership(customer, actorEmail);

        return orderRepository.findByCustomerId(customerId, pageable)
                .map(order -> DtoMapper.toOrderResponse(
                        order,
                        orderItemRepository.findByOrderId(order.getId()),
                        order.getDiscountAmount(),
                        order.getPromotion() == null ? null : order.getPromotion().getId()
                ));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, String actorEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        ensureCustomerOwnership(order.getCustomer(), actorEmail);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Shipped or delivered orders cannot be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        Order saved = orderRepository.save(order);
        auditService.log("Order", saved.getId(), "CANCEL", actorEmail, "Cancelled order " + saved.getOrderNumber());

        List<OrderItem> items = orderItemRepository.findByOrderId(saved.getId());
        return DtoMapper.toOrderResponse(saved, items, saved.getDiscountAmount(), saved.getPromotion() == null ? null : saved.getPromotion().getId());
    }

    private void ensureCustomerOwnership(Customer customer, String actorEmail) {
        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        boolean admin = actor.getRoles().contains(Role.ADMIN);
        if (!admin && !customer.getUser().getId().equals(actor.getId())) {
            throw new AccessDeniedException("You cannot access or modify other customers' orders.");
        }
    }
}
