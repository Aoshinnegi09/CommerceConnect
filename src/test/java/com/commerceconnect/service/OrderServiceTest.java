package com.commerceconnect.service;

import com.commerceconnect.dto.OrderItemRequest;
import com.commerceconnect.dto.OrderRequest;
import com.commerceconnect.dto.OrderResponse;
import com.commerceconnect.entity.*;
import com.commerceconnect.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private PromotionService promotionService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrderShouldReduceInventoryAndCreateOrder() {
        User actor = new User();
        actor.setId(10L);
        actor.setEmail("customer@example.com");
        actor.setRoles(Set.of(Role.CUSTOMER));

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUser(actor);

        Product product = new Product();
        product.setId(5L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("500.00"));
        product.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(actor));
        when(orderRepository.findByIdempotencyKeyAndCustomerId(any(), eq(1L))).thenReturn(Optional.empty());
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(inventoryRepository.decrementStock(5L, 2)).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            if (order.getId() == null) {
                order.setId(99L);
            }
            return order;
        });
        when(orderItemRepository.findByOrderId(99L)).thenReturn(List.of());

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        when(paymentService.process(any(Order.class), any(BigDecimal.class), any(String.class))).thenReturn(payment);

        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(5L, 2)), null);
        OrderResponse result = orderService.placeOrder(request, "customer@example.com", "idem-123");

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.status());
        verify(paymentService).process(any(Order.class), eq(new BigDecimal("1000.00")), eq("idem-123"));
    }
}
