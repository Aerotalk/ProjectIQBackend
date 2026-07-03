package com.grivetyglobals.invoiceiq.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class DatabaseCleanupConfig {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void cleanup() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS user_roles CASCADE");
            System.out.println("SUCCESSFULLY DROPPED OLD user_roles TABLE");
        } catch (Exception e) {
            System.err.println("Failed to drop table: " + e.getMessage());
        }
    }
}
