package com.commerceconnect.controller;

import com.commerceconnect.dto.NotificationRequest;
import com.commerceconnect.dto.NotificationResponse;
import com.commerceconnect.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<NotificationResponse> createNotification(@RequestParam Long userId,
                                                                   @Valid @RequestBody NotificationRequest request,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(notificationService.createForUser(userId, request, authentication.getName()));
    }

    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long userId, Authentication authentication) {
        return ResponseEntity.ok(notificationService.findByUser(userId, authentication.getName()));
    }
}
