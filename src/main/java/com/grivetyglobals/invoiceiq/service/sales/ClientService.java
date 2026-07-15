package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.ClientDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Client;
import com.grivetyglobals.invoiceiq.entity.sales.ClientAdditionalContact;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    public List<ClientDto> getClientsByCompany(UUID companyId) {
        return clientRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ClientDto getClient(UUID id) {
        return mapToDto(clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found")));
    }

    @Transactional
    public ClientDto createClient(UUID companyId, ClientDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Client client = new Client();
        client.setCompany(company);
        mapToEntity(dto, client);

        if (dto.getAdditionalContacts() != null) {
            List<ClientAdditionalContact> contacts = dto.getAdditionalContacts().stream().map(c -> {
                ClientAdditionalContact contact = new ClientAdditionalContact();
                contact.setClient(client);
                contact.setName(c.getName());
                contact.setDesignation(c.getDesignation());
                contact.setEmail(c.getEmail());
                contact.setPhone(c.getPhone());
                contact.setRole(c.getRole());
                return contact;
            }).collect(Collectors.toList());
            client.setAdditionalContacts(contacts);
        }

        return mapToDto(clientRepository.save(client));
    }

    @Transactional
    public ClientDto updateClient(UUID id, ClientDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        mapToEntity(dto, client);

        if (dto.getAdditionalContacts() != null) {
            client.getAdditionalContacts().clear();
            List<ClientAdditionalContact> contacts = dto.getAdditionalContacts().stream().map(c -> {
                ClientAdditionalContact contact = new ClientAdditionalContact();
                contact.setClient(client);
                contact.setName(c.getName());
                contact.setDesignation(c.getDesignation());
                contact.setEmail(c.getEmail());
                contact.setPhone(c.getPhone());
                contact.setRole(c.getRole());
                return contact;
            }).collect(Collectors.toList());
            client.getAdditionalContacts().addAll(contacts);
        }

        return mapToDto(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }

    private void mapToEntity(ClientDto dto, Client client) {
        client.setClientNo(dto.getClientNo());
        client.setCustomerType(dto.getCustomerType());
        client.setCompanyName(dto.getCompanyName());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setDisplayName(dto.getDisplayName());
        client.setGstTreatment(dto.getGstTreatment());
        client.setGstin(dto.getGstin());
        client.setPanNumber(dto.getPanNumber());
        client.setPlaceOfSupply(dto.getPlaceOfSupply());
        client.setSezUnitName(dto.getSezUnitName());
        client.setLutBondNo(dto.getLutBondNo());
        client.setCountry(dto.getCountry());
        client.setCurrency(dto.getCurrency());
        client.setForeignTaxId(dto.getForeignTaxId());
        client.setPrimaryContactPerson(dto.getPrimaryContactPerson());
        client.setDesignation(dto.getDesignation());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAlternatePhone(dto.getAlternatePhone());
        client.setBillingAttention(dto.getBillingAttention());
        client.setBillingPhone(dto.getBillingPhone());
        client.setBillingAddressLine1(dto.getBillingAddressLine1());
        client.setBillingAddressLine2(dto.getBillingAddressLine2());
        client.setBillingCity(dto.getBillingCity());
        client.setBillingState(dto.getBillingState());
        client.setBillingPinCode(dto.getBillingPinCode());
        client.setBillingCountry(dto.getBillingCountry());
        client.setSameAsBillingAddress(dto.getSameAsBillingAddress());
        client.setShippingAttention(dto.getShippingAttention());
        client.setShippingPhone(dto.getShippingPhone());
        client.setShippingAddressLine1(dto.getShippingAddressLine1());
        client.setShippingAddressLine2(dto.getShippingAddressLine2());
        client.setShippingCity(dto.getShippingCity());
        client.setShippingState(dto.getShippingState());
        client.setShippingPinCode(dto.getShippingPinCode());
        client.setShippingCountry(dto.getShippingCountry());
        client.setPaymentTerms(dto.getPaymentTerms());
        client.setCreditLimit(dto.getCreditLimit());
        client.setIndustry(dto.getIndustry());
        client.setNotes(dto.getNotes());
        client.setStatus(dto.getStatus());
    }

    private ClientDto mapToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setClientNo(client.getClientNo());
        dto.setCustomerType(client.getCustomerType());
        dto.setCompanyName(client.getCompanyName());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setDisplayName(client.getDisplayName());
        dto.setGstTreatment(client.getGstTreatment());
        dto.setGstin(client.getGstin());
        dto.setPanNumber(client.getPanNumber());
        dto.setPlaceOfSupply(client.getPlaceOfSupply());
        dto.setSezUnitName(client.getSezUnitName());
        dto.setLutBondNo(client.getLutBondNo());
        dto.setCountry(client.getCountry());
        dto.setCurrency(client.getCurrency());
        dto.setForeignTaxId(client.getForeignTaxId());
        dto.setPrimaryContactPerson(client.getPrimaryContactPerson());
        dto.setDesignation(client.getDesignation());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAlternatePhone(client.getAlternatePhone());
        dto.setBillingAttention(client.getBillingAttention());
        dto.setBillingPhone(client.getBillingPhone());
        dto.setBillingAddressLine1(client.getBillingAddressLine1());
        dto.setBillingAddressLine2(client.getBillingAddressLine2());
        dto.setBillingCity(client.getBillingCity());
        dto.setBillingState(client.getBillingState());
        dto.setBillingPinCode(client.getBillingPinCode());
        dto.setBillingCountry(client.getBillingCountry());
        dto.setSameAsBillingAddress(client.getSameAsBillingAddress());
        dto.setShippingAttention(client.getShippingAttention());
        dto.setShippingPhone(client.getShippingPhone());
        dto.setShippingAddressLine1(client.getShippingAddressLine1());
        dto.setShippingAddressLine2(client.getShippingAddressLine2());
        dto.setShippingCity(client.getShippingCity());
        dto.setShippingState(client.getShippingState());
        dto.setShippingPinCode(client.getShippingPinCode());
        dto.setShippingCountry(client.getShippingCountry());
        dto.setPaymentTerms(client.getPaymentTerms());
        dto.setCreditLimit(client.getCreditLimit());
        dto.setIndustry(client.getIndustry());
        dto.setNotes(client.getNotes());
        dto.setStatus(client.getStatus());

        if (client.getAdditionalContacts() != null) {
            dto.setAdditionalContacts(client.getAdditionalContacts().stream().map(c -> 
                com.grivetyglobals.invoiceiq.dto.sales.ClientAdditionalContactDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .designation(c.getDesignation())
                    .email(c.getEmail())
                    .phone(c.getPhone())
                    .role(c.getRole())
                    .build()
            ).collect(Collectors.toList()));
        }
        return dto;
    }
}
