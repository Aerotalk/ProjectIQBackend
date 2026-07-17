package com.grivetyglobals.invoiceiq;

import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QueryUserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindUser() {
        userRepository.findByEmail("reeya_2002@gmail.com").ifPresentOrElse(
            user -> {
                System.out.println("==================================================");
                System.out.println("USER FOUND!");
                System.out.println("Email: " + user.getEmail());
                System.out.println("Username field: " + user.getActualUsername());
                System.out.println("==================================================");
            },
            () -> {
                System.out.println("==================================================");
                System.out.println("USER NOT FOUND");
                System.out.println("==================================================");
            }
        );
    }
}
