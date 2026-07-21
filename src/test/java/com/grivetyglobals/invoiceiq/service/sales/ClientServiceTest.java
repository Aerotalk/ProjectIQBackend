package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.ClientDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Client;
import com.grivetyglobals.invoiceiq.exception.BusinessValidationException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ClientService clientService;

    private UUID companyId;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        testCompany = Company.builder().id(companyId).companyName("Test Corp").build();
    }

    @Test
    void createClient_DuplicateGstin_ThrowsBusinessValidationException() {
        ClientDto dto = new ClientDto();
        dto.setGstin("27AAAAA0000A1Z5");
        dto.setDisplayName("Test Client");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(clientRepository.existsByCompanyIdAndGstinIgnoreCase(companyId, "27AAAAA0000A1Z5")).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                clientService.createClient(companyId, dto)
        );

        assertTrue(ex.getMessage().contains("A client with GSTIN '27AAAAA0000A1Z5' already exists"));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_DuplicatePan_ThrowsBusinessValidationException() {
        ClientDto dto = new ClientDto();
        dto.setPanNumber("ABCDE1234F");
        dto.setDisplayName("Test Client");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(clientRepository.existsByCompanyIdAndPanNumberIgnoreCase(companyId, "ABCDE1234F")).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                clientService.createClient(companyId, dto)
        );

        assertTrue(ex.getMessage().contains("A client with PAN number 'ABCDE1234F' already exists"));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_DuplicateGstin_ThrowsBusinessValidationException() {
        UUID clientId = UUID.randomUUID();
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setCompany(testCompany);

        ClientDto dto = new ClientDto();
        dto.setGstin("27AAAAA0000A1Z5");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.existsByCompanyIdAndGstinIgnoreCaseAndIdNot(companyId, "27AAAAA0000A1Z5", clientId)).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                clientService.updateClient(clientId, dto)
        );

        assertTrue(ex.getMessage().contains("A client with GSTIN '27AAAAA0000A1Z5' already exists"));
        verify(clientRepository, never()).save(any());
    }
}
