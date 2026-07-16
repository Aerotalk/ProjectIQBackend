package com.grivetyglobals.invoiceiq.service.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.KBDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.ticket.KnowledgeBaseArticle;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.ticket.KBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KBService {

    private final KBRepository kbRepository;
    private final CompanyRepository companyRepository;
    private final KBMapper kbMapper;

    @Transactional(readOnly = true)
    public List<KBDto> getArticlesByCompany(UUID companyId) {
        return kbRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(kbMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KBDto getArticle(UUID id) {
        KnowledgeBaseArticle article = kbRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        return kbMapper.toDto(article);
    }

    public KBDto createArticle(UUID companyId, KBDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        KnowledgeBaseArticle article = kbMapper.toEntity(dto);
        article.setCompany(company);

        if (article.getArticleNo() == null || article.getArticleNo().isEmpty()) {
            article.setArticleNo("KB-" + System.currentTimeMillis());
        }

        KnowledgeBaseArticle saved = kbRepository.save(article);
        return kbMapper.toDto(saved);
    }

    public KBDto updateArticle(UUID id, KBDto dto) {
        KnowledgeBaseArticle existing = kbRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        kbMapper.updateEntityFromDto(dto, existing);
        KnowledgeBaseArticle updated = kbRepository.save(existing);
        return kbMapper.toDto(updated);
    }

    public void deleteArticle(UUID id) {
        kbRepository.deleteById(id);
    }
}
