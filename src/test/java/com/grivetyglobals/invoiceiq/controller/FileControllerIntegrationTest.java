package com.grivetyglobals.invoiceiq.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testDownloadFile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/files/37a00ee0-8058-4fb4-926c-f899047ec426"))
                .andDo(print())
                .andReturn();
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response Body: " + result.getResponse().getContentAsString());
    }
}
