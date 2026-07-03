package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.ActivityLog;
import com.grivetyglobals.invoiceiq.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ActivityLogRepository activityLogRepository;

    @Async
    public void logActivity(String action, String description, UUID entityId, String entityType, UUID userId, UUID organizationId) {
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .description(description)
                .entityId(entityId)
                .entityType(entityType)
                .userId(userId)
                .organizationId(organizationId)
                .build();
        
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentActivity(UUID organizationId) {
        return activityLogRepository.findTop10ByOrganizationIdOrderByTimestampDesc(organizationId);
    }

    public Page<ActivityLog> getPaginatedActivity(UUID organizationId, Pageable pageable) {
        return activityLogRepository.findByOrganizationIdOrderByTimestampDesc(organizationId, pageable);
    }
}
