package com.grivetyglobals.invoiceiq.security;

import com.grivetyglobals.invoiceiq.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Unauthorized: No authenticated user found");
    }

    public static UUID getCurrentOrganizationId() {
        User user = getCurrentUser();
        if (user.getOrganization() != null) {
            return user.getOrganization().getId();
        }
        return null; // System Super Admins do not have an organization
    }

    public static UUID getCurrentCompanyId() {
        User user = getCurrentUser();
        if (user.getCompany() != null) {
            return user.getCompany().getId();
        }
        return null; // Some users (like super admin or org admin) might not have a specific company
    }
}
