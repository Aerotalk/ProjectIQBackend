package com.grivetyglobals.invoiceiq.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;
import com.grivetyglobals.invoiceiq.entity.File;

@SpringBootTest
public class FileMetadataTest {

    @Autowired
    private FileService fileService;

    @Test
    public void testMetadata() throws Exception {
        UUID fileId = UUID.fromString("37a00ee0-8058-4fb4-926c-f899047ec426");
        File metadata = fileService.getFileMetadata(fileId);
        System.out.println("MimeType: " + metadata.getMimeType());
        System.out.println("OriginalName: " + metadata.getOriginalName());
        System.out.println("FileSize: " + metadata.getFileSize());
    }
}
