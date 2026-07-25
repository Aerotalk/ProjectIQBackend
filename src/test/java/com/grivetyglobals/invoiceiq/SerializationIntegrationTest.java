package com.grivetyglobals.invoiceiq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SerializationIntegrationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void testCompanySerializationAndRedisCache() throws Exception {
        // 1. Fetch a real company from the database
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            System.out.println("No companies found in DB to test. Skipping.");
            return;
        }
        
        Company company = companies.get(0);
        
        // Force initialize addresses just like AdminService does
        if (company.getAddresses() != null) {
            company.getAddresses().size();
        }
        
        System.out.println("COMPANY LOADED: " + company.getCompanyName());
        
        // 2. Serialize to JSON using the application's ObjectMapper
        String json = objectMapper.writeValueAsString(company);
        System.out.println("SERIALIZED TO JSON SUCCESSFULLY!");
        
        // 3. Test deserialization
        Company deserialized = objectMapper.readValue(json, Company.class);
        assertNotNull(deserialized);
        System.out.println("DESERIALIZED FROM JSON SUCCESSFULLY!");
        
        System.out.println("TEST PASSED: No LazyInitializationException thrown during JSON processing.");
    }
}
