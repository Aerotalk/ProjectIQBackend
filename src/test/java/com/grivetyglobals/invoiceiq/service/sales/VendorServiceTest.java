package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.VendorDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.exception.BusinessValidationException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.VendorRepository;
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
class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private VendorService vendorService;

    private UUID companyId;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        testCompany = Company.builder().id(companyId).companyName("Test Corp").build();
    }

    @Test
    void createVendor_DuplicateGstin_ThrowsBusinessValidationException() {
        VendorDto dto = new VendorDto();
        dto.setGstin("27AAAAA0000A1Z5");
        dto.setDisplayName("Test Vendor");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(vendorRepository.existsByCompanyIdAndGstinIgnoreCase(companyId, "27AAAAA0000A1Z5")).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                vendorService.createVendor(companyId, dto)
        );

        assertTrue(ex.getMessage().contains("A vendor with GSTIN '27AAAAA0000A1Z5' already exists"));
        verify(vendorRepository, never()).save(any());
    }

    @Test
    void createVendor_DuplicatePan_ThrowsBusinessValidationException() {
        VendorDto dto = new VendorDto();
        dto.setPanNumber("ABCDE1234F");
        dto.setDisplayName("Test Vendor");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(vendorRepository.existsByCompanyIdAndPanNumberIgnoreCase(companyId, "ABCDE1234F")).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                vendorService.createVendor(companyId, dto)
        );

        assertTrue(ex.getMessage().contains("A vendor with PAN number 'ABCDE1234F' already exists"));
        verify(vendorRepository, never()).save(any());
    }

    @Test
    void updateVendor_DuplicateGstin_ThrowsBusinessValidationException() {
        UUID vendorId = UUID.randomUUID();
        Vendor existingVendor = new Vendor();
        existingVendor.setId(vendorId);
        existingVendor.setCompany(testCompany);

        VendorDto dto = new VendorDto();
        dto.setGstin("27AAAAA0000A1Z5");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(existingVendor));
        when(vendorRepository.existsByCompanyIdAndGstinIgnoreCaseAndIdNot(companyId, "27AAAAA0000A1Z5", vendorId)).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                vendorService.updateVendor(vendorId, dto)
        );

        assertTrue(ex.getMessage().contains("A vendor with GSTIN '27AAAAA0000A1Z5' already exists"));
        verify(vendorRepository, never()).save(any());
    }
}
