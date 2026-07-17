package com.grivetyglobals.invoiceiq.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import java.util.UUID;

@SpringBootTest
public class FileServiceS3Test {

    @Autowired
    private FileService fileService;

    @Test
    public void testFetchFile() throws Exception {
        UUID fileId = UUID.fromString("37a00ee0-8058-4fb4-926c-f899047ec426");
        Resource resource = fileService.loadFileAsResource(fileId);
        System.out.println("Resource fetched! Content Length: " + resource.contentLength());
        System.out.println("Resource exists: " + resource.exists());
    }
}
