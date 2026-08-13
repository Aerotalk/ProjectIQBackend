package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.Competency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyDTO {
    private UUID id;
    private String name;
    private String description;
    private String category;
    private Integer weightage;
    private Boolean active;

    public static CompetencyDTO fromEntity(Competency competency) {
        if (competency == null) {
            return null;
        }
        
        return CompetencyDTO.builder()
                .id(competency.getId())
                .name(competency.getName())
                .description(competency.getDescription())
                .category(competency.getCategory())
                .weightage(competency.getWeightage())
                .active(competency.getActive())
                .build();
    }
}
