package com.commerceconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String notificationType
) {}
