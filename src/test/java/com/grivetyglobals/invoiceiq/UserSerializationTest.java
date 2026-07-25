package com.grivetyglobals.invoiceiq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserSerializationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void testUserSerialization() throws Exception {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;
        User user = users.get(0);
        Set<String> perms = permissionService.getEffectivePermissions(user);
        user.setEffectivePermissions(perms);
        
        System.out.println("EFFECTIVE PERMISSIONS BEFORE: " + user.getEffectivePermissions());
        System.out.println("AUTHORITIES BEFORE: " + user.getAuthorities());
        
        String json = objectMapper.writeValueAsString(user);
        System.out.println("USER JSON: " + json);
        
        User deserialized = objectMapper.readValue(json, User.class);
        System.out.println("EFFECTIVE PERMISSIONS AFTER: " + deserialized.getEffectivePermissions());
        System.out.println("AUTHORITIES AFTER: " + deserialized.getAuthorities());
    }
}
