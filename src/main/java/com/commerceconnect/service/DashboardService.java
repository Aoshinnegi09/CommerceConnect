package com.commerceconnect.service;

import com.commerceconnect.dto.DashboardSummary;
import com.commerceconnect.repository.CampaignRepository;
import com.commerceconnect.repository.CustomerRepository;
import com.commerceconnect.repository.OrderRepository;
import com.commerceconnect.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CampaignRepository campaignRepository;

    public DashboardService(CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           CampaignRepository campaignRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.campaignRepository = campaignRepository;
    }

    public DashboardSummary getSummary() {
        return new DashboardSummary(
                customerRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                campaignRepository.count(),
                BigDecimal.ZERO
        );
    }
}
