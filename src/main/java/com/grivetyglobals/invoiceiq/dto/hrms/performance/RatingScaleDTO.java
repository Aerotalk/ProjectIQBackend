package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.RatingScale;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.RatingScaleLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingScaleDTO {
    private UUID id;
    private String name;
    private Integer minRating;
    private Integer maxRating;
    private String description;
    private List<RatingScaleLevelDTO> levels;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingScaleLevelDTO {
        private UUID id;
        private Integer value;
        private String label;
        private String description;

        public static RatingScaleLevelDTO fromEntity(RatingScaleLevel level) {
            if (level == null) return null;
            return RatingScaleLevelDTO.builder()
                    .id(level.getId())
                    .value(level.getValue())
                    .label(level.getLabel())
                    .description(level.getDescription())
                    .build();
        }
    }

    public static RatingScaleDTO fromEntity(RatingScale scale) {
        if (scale == null) {
            return null;
        }

        List<RatingScaleLevelDTO> levelDTOs = null;
        if (scale.getLevels() != null) {
            levelDTOs = scale.getLevels().stream()
                    .map(RatingScaleLevelDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        return RatingScaleDTO.builder()
                .id(scale.getId())
                .name(scale.getName())
                .minRating(scale.getMinRating())
                .maxRating(scale.getMaxRating())
                .description(scale.getDescription())
                .levels(levelDTOs)
                .build();
    }
}
