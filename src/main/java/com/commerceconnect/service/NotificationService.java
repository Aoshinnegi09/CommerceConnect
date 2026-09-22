package com.commerceconnect.service;

import com.commerceconnect.dto.NotificationRequest;
import com.commerceconnect.dto.NotificationResponse;
import com.commerceconnect.entity.*;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.NotificationRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationResponse createForUser(Long userId, NotificationRequest request, String actorEmail) {
        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        boolean admin = actor.getRoles().contains(Role.ADMIN);
        if (!admin && !actor.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot create notifications for other users.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setNotificationType(parseType(request.notificationType()));
        notification.setNotificationStatus(NotificationStatus.SENT);
        return DtoMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    public List<NotificationResponse> findByUser(Long userId, String actorEmail) {
        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        boolean admin = actor.getRoles().contains(Role.ADMIN);
        if (!admin && !actor.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access notifications for other users.");
        }
        return notificationRepository.findByUserId(userId).stream().map(DtoMapper::toNotificationResponse).toList();
    }

    private NotificationType parseType(String value) {
        try {
            return NotificationType.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid notification type.");
        }
    }
}
