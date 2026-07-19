package com.grivetyglobals.invoiceiq.config;

import com.grivetyglobals.invoiceiq.entity.DocumentTemplate;
import com.grivetyglobals.invoiceiq.repository.DocumentTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DocumentTemplateSeeder implements CommandLineRunner {

    private final DocumentTemplateRepository documentTemplateRepository;
    private static final String TEMPLATES_DIR = "Document_Templates";

    private Path getTemplatesDirPath() {
        Path path1 = Paths.get(TEMPLATES_DIR);
        if (Files.exists(path1) && Files.isDirectory(path1)) {
            return path1;
        }
        Path path2 = Paths.get("ProjectIQBackend", TEMPLATES_DIR);
        if (Files.exists(path2) && Files.isDirectory(path2)) {
            return path2;
        }
        return path1;
    }

    @Override
    public void run(String... args) throws Exception {
        Path dirPath = getTemplatesDirPath();
        if (!Files.exists(dirPath)) {
            System.out.println("⚠️ Document_Templates directory not found, skipping template seeder.");
            return;
        }

        System.out.println("🚀 Seeding Document Templates into the database...");

        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".html"))
                  .forEach(this::processTemplateFile);
        } catch (IOException e) {
            System.err.println("❌ Failed to read templates directory: " + e.getMessage());
        }
        
        System.out.println("✅ Document Templates seeding completed.");
    }

    private void processTemplateFile(Path filePath) {
        String filename = filePath.getFileName().toString();
        
        // Skip if already in DB
        Optional<DocumentTemplate> existing = documentTemplateRepository.findByFilename(filename);
        if (existing.isPresent()) {
            return;
        }

        try {
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            String name = generateBeautifulName(filename);
            String type = determineType(filename);

            DocumentTemplate template = DocumentTemplate.builder()
                    .filename(filename)
                    .name(name)
                    .type(type)
                    .content(content)
                    .build();

            documentTemplateRepository.save(template);
            System.out.println("Added template: " + name + " (" + filename + ")");
        } catch (IOException e) {
            System.err.println("❌ Failed to read template content for " + filename + ": " + e.getMessage());
        }
    }

    private String generateBeautifulName(String filename) {
        String name = filename.replace(".html", "").replace("_", " ").replace("-", " ");
        // Title case logic
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        name = sb.toString().trim();
        
        // Special case overrides for default templates to make them look nice
        if (name.equalsIgnoreCase("Quotation")) return "Standard Quotation";
        if (name.equalsIgnoreCase("Invoice")) return "Standard Invoice";
        if (name.equalsIgnoreCase("Purchase Order")) return "Standard Purchase Order";
        if (name.equalsIgnoreCase("Delivery Challan")) return "Standard Delivery Challan";
        if (name.equalsIgnoreCase("Bill")) return "Standard Bill";
        
        return name;
    }

    private String determineType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.contains("quotation")) return "quotation";
        if (lower.contains("invoice")) return "invoice";
        if (lower.contains("purchase_order") || lower.contains("purchase order")) return "purchase_order";
        if (lower.contains("challan")) return "challan";
        if (lower.contains("bill")) return "bill";
        return "other";
    }
}
