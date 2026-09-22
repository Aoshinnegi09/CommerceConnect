package com.commerceconnect.dto;

public record NotificationRequest(
        String title,
        String message,
        String notificationType
) {}
