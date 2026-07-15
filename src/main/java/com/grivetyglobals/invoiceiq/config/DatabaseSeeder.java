package com.grivetyglobals.invoiceiq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Comparator;

@Configuration
public class DatabaseSeeder {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            try {
                // Check if permissions have already been seeded
                Integer permCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM permissions", Integer.class);
                
                if (permCount != null && permCount > 0) {
                    System.out.println("✅ Database already seeded. Skipping initial data injection.");
                    return;
                }

                System.out.println("🚀 No permissions found. Running manual database seeding (Flyway replacement)...");

                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources("classpath:db/migration/*.sql");

                // Sort resources to ensure V2 runs before V3, etc.
                Arrays.sort(resources, Comparator.comparing(Resource::getFilename));

                for (Resource resource : resources) {
                    System.out.println("Executing script: " + resource.getFilename());
                    try {
                        String sql = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                        jdbcTemplate.execute(sql);
                    } catch (Exception ex) {
                        System.err.println("⚠️ Warning while executing " + resource.getFilename() + ": " + ex.getMessage());
                    }
                }

                System.out.println("✅ Database seeding completed successfully!");

            } catch (Exception e) {
                System.err.println("❌ Error during database seeding: " + e.getMessage());
                // Don't print stack trace on failure if the table doesn't exist yet, just warn
            }
        };
    }
}
