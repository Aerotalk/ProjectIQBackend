package com.grivetyglobals.invoiceiq.service.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.TicketDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.ticket.Ticket;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.ticket.TicketRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository;
    private final TicketMapper ticketMapper;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByCompany(UUID companyId) {
        return ticketRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toDto(ticket);
    }

    public TicketDto createTicket(UUID companyId, TicketDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Ticket ticket = ticketMapper.toEntity(dto);
        ticket.setCompany(company);

        // Auto-generate ticket number if not provided
        if (ticket.getTicketNo() == null || ticket.getTicketNo().isEmpty()) {
            ticket.setTicketNo("TKT-" + System.currentTimeMillis()); // simple fallback, can be improved with sequence
        }

        Ticket saved = ticketRepository.save(ticket);
        
        if (saved.getProject() != null && saved.getProject().getId() != null) {
            com.grivetyglobals.invoiceiq.entity.project.Project project = projectRepository.findById(saved.getProject().getId()).orElse(null);
            if (project != null) {
                if (project.getLinkedIncidents() == null) {
                    project.setLinkedIncidents(new java.util.ArrayList<>());
                }
                if (!project.getLinkedIncidents().contains(saved.getId().toString())) {
                    project.getLinkedIncidents().add(saved.getId().toString());
                    projectRepository.save(project);
                }
            }
        }
        
        return ticketMapper.toDto(saved);
    }

    public TicketDto updateTicket(UUID id, TicketDto dto) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticketMapper.updateEntityFromDto(dto, existing);
        Ticket updated = ticketRepository.save(existing);
        return ticketMapper.toDto(updated);
    }

    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
