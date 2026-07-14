package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.VendorDto;
import com.grivetyglobals.invoiceiq.dto.sales.VendorBankDetailsDto;
import com.grivetyglobals.invoiceiq.dto.sales.VendorAdditionalContactDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.entity.sales.VendorAdditionalContact;
import com.grivetyglobals.invoiceiq.entity.sales.VendorBankDetails;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final CompanyRepository companyRepository;

    public List<VendorDto> getVendorsByCompany(UUID companyId) {
        return vendorRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public VendorDto getVendor(UUID id) {
        return mapToDto(vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found")));
    }

    @Transactional
    public VendorDto createVendor(UUID companyId, VendorDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Vendor vendor = new Vendor();
        vendor.setCompany(company);
        mapToEntity(dto, vendor);

        handleContactsAndBankDetails(dto, vendor);

        return mapToDto(vendorRepository.save(vendor));
    }

    @Transactional
    public VendorDto updateVendor(UUID id, VendorDto dto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        mapToEntity(dto, vendor);

        if (vendor.getAdditionalContacts() != null) {
            vendor.getAdditionalContacts().clear();
        }
        
        handleContactsAndBankDetails(dto, vendor);

        return mapToDto(vendorRepository.save(vendor));
    }

    @Transactional
    public void deleteVendor(UUID id) {
        vendorRepository.deleteById(id);
    }

    private void handleContactsAndBankDetails(VendorDto dto, Vendor vendor) {
        if (dto.getAdditionalContacts() != null) {
            List<VendorAdditionalContact> contacts = dto.getAdditionalContacts().stream().map(c -> {
                VendorAdditionalContact contact = new VendorAdditionalContact();
                contact.setVendor(vendor);
                contact.setName(c.getName());
                contact.setDesignation(c.getDesignation());
                contact.setEmail(c.getEmail());
                contact.setPhone(c.getPhone());
                contact.setRole(c.getRole());
                return contact;
            }).collect(Collectors.toList());
            if (vendor.getAdditionalContacts() == null) {
                vendor.setAdditionalContacts(contacts);
            } else {
                vendor.getAdditionalContacts().addAll(contacts);
            }
        }

        if (dto.getBankDetails() != null) {
            VendorBankDetails bank = vendor.getBankDetails();
            if (bank == null) {
                bank = new VendorBankDetails();
                bank.setVendor(vendor);
            }
            bank.setAccountName(dto.getBankDetails().getAccountName());
            bank.setAccountNumber(dto.getBankDetails().getAccountNumber());
            bank.setIfscCode(dto.getBankDetails().getIfscCode());
            bank.setBankName(dto.getBankDetails().getBankName());
            bank.setBranchName(dto.getBankDetails().getBranchName());
            bank.setSwiftCode(dto.getBankDetails().getSwiftCode());
            vendor.setBankDetails(bank);
        } else {
            vendor.setBankDetails(null);
        }
    }

    private void mapToEntity(VendorDto dto, Vendor vendor) {
        vendor.setVendorNo(dto.getVendorNo());
        vendor.setVendorType(dto.getVendorType());
        vendor.setCompanyName(dto.getCompanyName());
        vendor.setFirstName(dto.getFirstName());
        vendor.setLastName(dto.getLastName());
        vendor.setDisplayName(dto.getDisplayName());
        vendor.setGstTreatment(dto.getGstTreatment());
        vendor.setGstin(dto.getGstin());
        vendor.setPanNumber(dto.getPanNumber());
        vendor.setPlaceOfSupply(dto.getPlaceOfSupply());
        vendor.setSezUnitName(dto.getSezUnitName());
        vendor.setLutBondNo(dto.getLutBondNo());
        vendor.setCountry(dto.getCountry());
        vendor.setCurrency(dto.getCurrency());
        vendor.setForeignTaxId(dto.getForeignTaxId());
        vendor.setPrimaryContactPerson(dto.getPrimaryContactPerson());
        vendor.setDesignation(dto.getDesignation());
        vendor.setEmail(dto.getEmail());
        vendor.setPhone(dto.getPhone());
        vendor.setAlternatePhone(dto.getAlternatePhone());
        vendor.setBillingAddressLine1(dto.getBillingAddressLine1());
        vendor.setBillingAddressLine2(dto.getBillingAddressLine2());
        vendor.setBillingCity(dto.getBillingCity());
        vendor.setBillingState(dto.getBillingState());
        vendor.setBillingPinCode(dto.getBillingPinCode());
        vendor.setBillingCountry(dto.getBillingCountry());
        vendor.setSameAsBillingAddress(dto.getSameAsBillingAddress());
        vendor.setShippingAddressLine1(dto.getShippingAddressLine1());
        vendor.setShippingAddressLine2(dto.getShippingAddressLine2());
        vendor.setShippingCity(dto.getShippingCity());
        vendor.setShippingState(dto.getShippingState());
        vendor.setShippingPinCode(dto.getShippingPinCode());
        vendor.setShippingCountry(dto.getShippingCountry());
        vendor.setPaymentTerms(dto.getPaymentTerms());
        vendor.setCreditLimit(dto.getCreditLimit());
        vendor.setNotes(dto.getNotes());
        vendor.setTdsPercentage(dto.getTdsPercentage());
        vendor.setReverseCharge(dto.getReverseCharge());
        vendor.setStatus(dto.getStatus());
    }

    private VendorDto mapToDto(Vendor vendor) {
        VendorDto dto = new VendorDto();
        dto.setId(vendor.getId());
        dto.setVendorNo(vendor.getVendorNo());
        dto.setVendorType(vendor.getVendorType());
        dto.setCompanyName(vendor.getCompanyName());
        dto.setFirstName(vendor.getFirstName());
        dto.setLastName(vendor.getLastName());
        dto.setDisplayName(vendor.getDisplayName());
        dto.setGstTreatment(vendor.getGstTreatment());
        dto.setGstin(vendor.getGstin());
        dto.setPanNumber(vendor.getPanNumber());
        dto.setPlaceOfSupply(vendor.getPlaceOfSupply());
        dto.setSezUnitName(vendor.getSezUnitName());
        dto.setLutBondNo(vendor.getLutBondNo());
        dto.setCountry(vendor.getCountry());
        dto.setCurrency(vendor.getCurrency());
        dto.setForeignTaxId(vendor.getForeignTaxId());
        dto.setPrimaryContactPerson(vendor.getPrimaryContactPerson());
        dto.setDesignation(vendor.getDesignation());
        dto.setEmail(vendor.getEmail());
        dto.setPhone(vendor.getPhone());
        dto.setAlternatePhone(vendor.getAlternatePhone());
        dto.setBillingAddressLine1(vendor.getBillingAddressLine1());
        dto.setBillingAddressLine2(vendor.getBillingAddressLine2());
        dto.setBillingCity(vendor.getBillingCity());
        dto.setBillingState(vendor.getBillingState());
        dto.setBillingPinCode(vendor.getBillingPinCode());
        dto.setBillingCountry(vendor.getBillingCountry());
        dto.setSameAsBillingAddress(vendor.getSameAsBillingAddress());
        dto.setShippingAddressLine1(vendor.getShippingAddressLine1());
        dto.setShippingAddressLine2(vendor.getShippingAddressLine2());
        dto.setShippingCity(vendor.getShippingCity());
        dto.setShippingState(vendor.getShippingState());
        dto.setShippingPinCode(vendor.getShippingPinCode());
        dto.setShippingCountry(vendor.getShippingCountry());
        dto.setPaymentTerms(vendor.getPaymentTerms());
        dto.setCreditLimit(vendor.getCreditLimit());
        dto.setNotes(vendor.getNotes());
        dto.setTdsPercentage(vendor.getTdsPercentage());
        dto.setReverseCharge(vendor.getReverseCharge());
        dto.setStatus(vendor.getStatus());

        if (vendor.getAdditionalContacts() != null) {
            dto.setAdditionalContacts(vendor.getAdditionalContacts().stream().map(c -> 
                VendorAdditionalContactDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .designation(c.getDesignation())
                    .email(c.getEmail())
                    .phone(c.getPhone())
                    .role(c.getRole())
                    .build()
            ).collect(Collectors.toList()));
        }

        if (vendor.getBankDetails() != null) {
            dto.setBankDetails(VendorBankDetailsDto.builder()
                .id(vendor.getBankDetails().getId())
                .accountName(vendor.getBankDetails().getAccountName())
                .accountNumber(vendor.getBankDetails().getAccountNumber())
                .ifscCode(vendor.getBankDetails().getIfscCode())
                .bankName(vendor.getBankDetails().getBankName())
                .branchName(vendor.getBankDetails().getBranchName())
                .swiftCode(vendor.getBankDetails().getSwiftCode())
                .build());
        }

        return dto;
    }
}
