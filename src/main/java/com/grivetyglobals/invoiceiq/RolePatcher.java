package com.grivetyglobals.invoiceiq;
import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class RolePatcher implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public RolePatcher(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        Optional<User> optUser = userRepository.findByEmail("jug@tcs.com");
        if (optUser.isPresent()) {
            User user = optUser.get();
            Role employeeRole = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .roleName("ROLE_EMPLOYEE")
                            .systemRole(true)
                            .description("Employee")
                            .status("ACTIVE")
                            .build()));
                            
            boolean hasRole = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getRoleName().equals("ROLE_EMPLOYEE"));
                
            if (!hasRole) {
                user.getUserRoles().add(UserRole.builder().user(user).role(employeeRole).build());
                userRepository.save(user);
                System.out.println(">>> SUCCESSFULLY ADDED ROLE_EMPLOYEE TO jug@tcs.com <<<");
            } else {
                System.out.println(">>> jug@tcs.com ALREADY HAS ROLE_EMPLOYEE <<<");
            }
        }
    }
}
