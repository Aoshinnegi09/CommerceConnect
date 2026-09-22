package com.commerceconnect.dto;

import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @Size(max = 30) String phone,
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @Size(max = 100) String country
) {}
