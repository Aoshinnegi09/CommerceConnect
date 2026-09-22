package com.commerceconnect.controller;

import com.commerceconnect.dto.NotificationRequest;
import com.commerceconnect.entity.NotificationEntity;
import com.commerceconnect.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<NotificationEntity> createNotification(@RequestParam Long userId,
                                                                @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createForUser(userId, request));
    }

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<NotificationEntity>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUser(userId));
    }
}
