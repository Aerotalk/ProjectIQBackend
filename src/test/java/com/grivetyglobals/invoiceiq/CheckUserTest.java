package com.grivetyglobals.invoiceiq;

import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.stream.Collectors;

import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CheckUserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Commit
    public void printUser() {
        User user = userRepository.findByEmail("reeya_2002@gmail.com").orElse(null);
        if (user != null) {
            System.out.println("====== USER DETAILS BEFORE ======");
            System.out.println("Email: " + user.getEmail());
            System.out.println("Username: " + user.getActualUsername());
            System.out.println("Roles: " + user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).collect(Collectors.joining(", ")));
            System.out.println("==========================");

            // Remove ROLE_SUPER_ADMIN
            user.getUserRoles().removeIf(ur -> ur.getRole().getRoleName().equals("ROLE_SUPER_ADMIN"));
            userRepository.save(user);

            System.out.println("ROLE_SUPER_ADMIN removed!");
            
            System.out.println("====== USER DETAILS AFTER ======");
            System.out.println("Roles: " + user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).collect(Collectors.joining(", ")));
            System.out.println("==========================");
        } else {
            System.out.println("User not found!");
        }
    }
}
