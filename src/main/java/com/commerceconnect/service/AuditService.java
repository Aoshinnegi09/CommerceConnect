package com.commerceconnect.service;

import com.commerceconnect.entity.AuditLog;
import com.commerceconnect.entity.User;
import com.commerceconnect.repository.AuditLogRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void log(String entityName, Long entityId, String action, String actorEmail, String details) {
        AuditLog log = new AuditLog();
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setDetails(details);

        if (actorEmail != null && !actorEmail.isBlank()) {
            userRepository.findByEmail(actorEmail).ifPresent(log::setPerformedBy);
        }

        auditLogRepository.save(log);
    }
}
