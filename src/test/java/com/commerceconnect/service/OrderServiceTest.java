package com.commerceconnect.service;

import com.commerceconnect.dto.OrderItemRequest;
import com.commerceconnect.dto.OrderRequest;
import com.commerceconnect.entity.*;
import com.commerceconnect.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

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
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrderShouldReduceInventoryAndCreateOrder() {
        Customer customer = new Customer();
        customer.setId(1L);
        User user = new User();
        user.setId(10L);
        user.setEmail("customer@example.com");
        customer.setUser(user);

        Product product = new Product();
        product.setId(5L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("500.00"));
        product.setActive(true);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(12);

        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));
        when(productRepository.findById(5L)).thenReturn(java.util.Optional.of(product));
        when(inventoryRepository.findByProductId(5L)).thenReturn(java.util.Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setId(99L);
            return order;
        });

        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(5L, 2)));
        Order result = orderService.placeOrder(request);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(10, inventory.getQuantity());
        verify(paymentRepository).save(any(Payment.class));
    }
}
