package com.commerceconnect.dto;

import com.commerceconnect.entity.NotificationStatus;
import com.commerceconnect.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long userId,
        String title,
        String message,
        NotificationType notificationType,
        NotificationStatus notificationStatus,
        boolean read,
        LocalDateTime createdAt
) {}
