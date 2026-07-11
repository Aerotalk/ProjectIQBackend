package com.grivetyglobals.invoiceiq.config;

import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser != null) {
                return Optional.ofNullable(currentUser.getId());
            }
        } catch (Exception e) {
            // Ignore if no security context is available (e.g., during startup/seeding)
        }
        return Optional.empty();
    }
}
