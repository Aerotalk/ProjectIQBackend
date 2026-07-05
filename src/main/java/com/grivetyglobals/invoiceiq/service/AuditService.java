package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.AuditLog;
import com.grivetyglobals.invoiceiq.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // Backwards-compatible method that extracts request headers synchronously
    public void logActivity(String action, String description, UUID entityId, String entityName, UUID userId, UUID organizationId) {
        String ipAddress = null;
        String userAgent = null;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                ipAddress = attrs.getRequest().getRemoteAddr();
                userAgent = attrs.getRequest().getHeader("User-Agent");
            }
        } catch (Exception e) {
            // Ignore if not in request context
        }
        
        asyncLogActivity(action, entityId, entityName, null, Map.of("description", description), userId, organizationId, ipAddress, userAgent);
    }

    public void logActivityWithValues(String action, UUID entityId, String entityName, Map<String, Object> oldValue, Map<String, Object> newValue, UUID userId, UUID organizationId) {
        String ipAddress = null;
        String userAgent = null;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                ipAddress = attrs.getRequest().getRemoteAddr();
                userAgent = attrs.getRequest().getHeader("User-Agent");
            }
        } catch (Exception e) {
            // Ignore if not in request context
        }
        
        asyncLogActivity(action, entityId, entityName, oldValue, newValue, userId, organizationId, ipAddress, userAgent);
    }

    @Async
    protected void asyncLogActivity(String action, UUID entityId, String entityName, Map<String, Object> oldValue, Map<String, Object> newValue, UUID userId, UUID organizationId, String ipAddress, String userAgent) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityId(entityId)
                .entityName(entityName)
                .oldValue(oldValue)
                .newValue(newValue)
                .userId(userId)
                .organizationId(organizationId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        
        auditLogRepository.save(log);
    }

    public Page<AuditLog> getPaginatedActivity(UUID organizationId, Pageable pageable) {
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }
    
    public List<AuditLog> getRecentActivity(UUID organizationId) {
        // Fallback to top 10 logic. Since we don't have a top 10 method in the repository yet, let's use pageable
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, org.springframework.data.domain.PageRequest.of(0, 10)).getContent();
    }
}
