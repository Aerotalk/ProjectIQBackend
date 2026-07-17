package com.grivetyglobals.invoiceiq;

import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.UserRole;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class UpdateRoleTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testUpdateRole() {
        userRepository.findByEmail("reeya_2002@gmail.com").ifPresentOrElse(
            user -> {
                Role superAdminRole = roleRepository.findByRoleName("ROLE_SUPER_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("ROLE_SUPER_ADMIN");
                        return roleRepository.save(r);
                    });
                
                boolean hasRole = user.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getRoleName().equals("ROLE_SUPER_ADMIN"));
                
                if (!hasRole) {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(superAdminRole);
                    userRoleRepository.save(userRole);
                    
                    System.out.println("==================================================");
                    System.out.println("ROLE_SUPER_ADMIN ADDED TO USER!");
                    System.out.println("==================================================");
                } else {
                    System.out.println("==================================================");
                    System.out.println("USER ALREADY HAS ROLE_SUPER_ADMIN");
                    System.out.println("==================================================");
                }
            },
            () -> {
                System.out.println("==================================================");
                System.out.println("USER NOT FOUND");
                System.out.println("==================================================");
            }
        );
    }
}
