package com.grivetyglobals.invoiceiq.service.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.KBDto;
import com.grivetyglobals.invoiceiq.entity.ticket.KnowledgeBaseArticle;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class KBMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public KBDto toDto(KnowledgeBaseArticle entity) {
        if (entity == null) return null;

        List<String> tagsList = new ArrayList<>();
        if (entity.getTags() != null && !entity.getTags().isEmpty()) {
            try {
                tagsList = objectMapper.readValue(entity.getTags(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                // If it fails to parse as JSON, maybe it's just a comma separated string
                tagsList = List.of(entity.getTags().split(","));
            }
        }

        return KBDto.builder()
                .id(entity.getId())
                .articleNo(entity.getArticleNo())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .content(entity.getContent())
                .author(entity.getAuthor())
                .status(entity.getStatus())
                .tags(tagsList)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public KnowledgeBaseArticle toEntity(KBDto dto) {
        if (dto == null) return null;

        String tagsJson = null;
        if (dto.getTags() != null) {
            try {
                tagsJson = objectMapper.writeValueAsString(dto.getTags());
            } catch (JsonProcessingException e) {
                tagsJson = String.join(",", dto.getTags());
            }
        }

        return KnowledgeBaseArticle.builder()
                .id(dto.getId())
                .articleNo(dto.getArticleNo())
                .title(dto.getTitle())
                .category(dto.getCategory())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .status(dto.getStatus())
                .tags(tagsJson)
                .build();
    }

    public void updateEntityFromDto(KBDto dto, KnowledgeBaseArticle entity) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getAuthor() != null) entity.setAuthor(dto.getAuthor());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        
        if (dto.getTags() != null) {
            try {
                entity.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                entity.setTags(String.join(",", dto.getTags()));
            }
        }
    }
}
