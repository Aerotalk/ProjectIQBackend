package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "document_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(nullable = false)
    private String type; // e.g. "quotation", "invoice", "purchase_order", "challan"

    @Column(columnDefinition = "TEXT")
    private String content;

}
