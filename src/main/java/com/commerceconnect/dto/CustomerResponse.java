package com.commerceconnect.dto;

public record CustomerResponse(
        Long id,
        Long userId,
        String email,
        String firstName,
        String lastName,
        String phone,
        String address,
        String city,
        String state,
        String country
) {}
