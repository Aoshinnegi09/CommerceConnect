package com.commerceconnect.dto;

public record CustomerRequest(
        String phone,
        String address,
        String city,
        String state,
        String country
) {}
