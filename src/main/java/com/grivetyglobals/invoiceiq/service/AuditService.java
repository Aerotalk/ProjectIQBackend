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

/**
 * Service class for handling audit logging.
 * Provides methods for asynchronously logging user activities and retrieving paginated activity logs.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Backwards-compatible method that extracts request headers synchronously
     * and logs the activity asynchronously without old/new values.
     * 
     * @param action         the action performed (e.g., 'COMPANY_CREATED')
     * @param description    a human-readable description of the action
     * @param entityId       the ID of the entity being acted upon
     * @param entityName     the name of the entity type (e.g., 'Company')
     * @param userId         the ID of the user performing the action
     * @param organizationId the ID of the organization the action belongs to
     */
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

    /**
     * Synchronously extracts request headers and logs the activity asynchronously
     * with detailed old and new values for field tracking.
     * 
     * @param action         the action performed
     * @param entityId       the ID of the entity being acted upon
     * @param entityName     the name of the entity type
     * @param oldValue       a map representing the entity state before the action
     * @param newValue       a map representing the entity state after the action
     * @param userId         the ID of the user performing the action
     * @param organizationId the ID of the organization the action belongs to
     */
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

    /**
     * Retrieves paginated activity logs for the current organization.
     * 
     * @param pageable pagination and sorting parameters
     * @return a Page of AuditLog entities
     */
    public Page<AuditLog> getPaginatedActivity(Pageable pageable) {
        UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }
    
    /**
     * Retrieves the 10 most recent activity logs for the current organization.
     * 
     * @return a list of recent AuditLog entities
     */
    public List<AuditLog> getRecentActivity() {
        UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        // Fallback to top 10 logic. Since we don't have a top 10 method in the repository yet, let's use pageable
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, org.springframework.data.domain.PageRequest.of(0, 10)).getContent();
    }
}
