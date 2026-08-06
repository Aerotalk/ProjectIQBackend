package com.grivetyglobals.invoiceiq.entity.hrms.performance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prf_rating_scale_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RatingScaleLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_scale_id", nullable = false)
    private RatingScale ratingScale;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "label", length = 100, nullable = false)
    private String label;

    @Column(name = "description", length = 300)
    private String description;
}
