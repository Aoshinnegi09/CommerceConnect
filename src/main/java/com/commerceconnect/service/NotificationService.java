package com.commerceconnect.service;

import com.commerceconnect.dto.NotificationRequest;
import com.commerceconnect.entity.NotificationEntity;
import com.commerceconnect.entity.User;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.NotificationRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationEntity createForUser(Long userId, NotificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setNotificationType(request.notificationType());
        return notificationRepository.save(notification);
    }

    public List<NotificationEntity> findByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
