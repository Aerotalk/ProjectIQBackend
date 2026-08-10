package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.*;
import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.repository.*;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service handling all employee sub-resource sections:
 * Address, EmergencyContact, Statutory, BankAccount, Documents,
 * SalaryRevision, Education, Family, Contract.
 */
@Service
@RequiredArgsConstructor
public class EmployeeDetailService {

    private final EmployeeService employeeService;
    private final EmployeeAddressRepository addressRepository;
    private final EmployeeEmergencyContactRepository emergencyContactRepository;
    private final EmployeeStatutoryRepository statutoryRepository;
    private final EmployeeBankAccountRepository bankAccountRepository;
    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeSalaryRevisionRepository salaryRevisionRepository;
    private final EmployeeEducationRepository educationRepository;
    private final EmployeeFamilyRepository familyRepository;
    private final EmployeeContractRepository contractRepository;
    private final EmployeePositionChangeRepository positionChangeRepository;
    private final EmployeeSeparationRepository separationRepository;

    // ─────────────────────────────────────────────
    // Address
    // ─────────────────────────────────────────────

    @Transactional
    public List<EmployeeAddress> saveAddress(UUID employeeId, EmployeeAddressRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Delete existing address rows for this employee and replace
        addressRepository.deleteByEmployeeId(employeeId);

        EmployeeAddress present = EmployeeAddress.builder()
                .employee(employee)
                .addressType("PRESENT")
                .country(req.getPresentCountry())
                .state(req.getPresentState())
                .city(req.getPresentCity())
                .addressLine1(req.getPresentAddressLine1())
                .addressLine2(req.getPresentAddressLine2())
                .pinCode(req.getPresentPinCode())
                .phone(req.getPresentPhone())
                .build();

        EmployeeAddress permanent = EmployeeAddress.builder()
                .employee(employee)
                .addressType("PERMANENT")
                .country(req.getPermanentCountry())
                .state(req.getPermanentState())
                .city(req.getPermanentCity())
                .addressLine1(req.getPermanentAddressLine1())
                .addressLine2(req.getPermanentAddressLine2())
                .pinCode(req.getPermanentPinCode())
                .phone(req.getPermanentPhone())
                .build();

        return addressRepository.saveAll(List.of(present, permanent));
    }

    @Transactional(readOnly = true)
    public List<EmployeeAddress> getAddress(UUID employeeId) {
        employeeService.getEmployeeById(employeeId); // permission check
        return addressRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Emergency Contact
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeEmergencyContact saveEmergencyContact(UUID employeeId, EmployeeEmergencyContactRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Upsert: one primary emergency contact per employee
        EmployeeEmergencyContact contact = emergencyContactRepository
                .findFirstByEmployeeId(employeeId)
                .orElse(EmployeeEmergencyContact.builder().employee(employee).build());

        contact.setName(req.getName());
        contact.setRelationship(req.getRelationship());
        contact.setPhone(req.getPhone());
        contact.setAlternatePhone(req.getAlternatePhone());
        contact.setEmail(req.getEmail());
        contact.setAddress(req.getAddress());
        contact.setPrimaryContact(req.getPrimaryContact() != null ? req.getPrimaryContact() : true);

        return emergencyContactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    public List<EmployeeEmergencyContact> getEmergencyContacts(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return emergencyContactRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Statutory
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeStatutory saveStatutory(UUID employeeId, EmployeeStatutoryRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        EmployeeStatutory statutory = statutoryRepository
                .findByEmployeeId(employeeId)
                .orElse(EmployeeStatutory.builder().employee(employee).build());

        statutory.setPanNumber(req.getPanNumber());
        statutory.setAadhaarNumber(req.getAadhaarNumber());
        statutory.setUan(req.getUan());
        statutory.setPfNumber(req.getPfNumber());
        statutory.setEsiNumber(req.getEsiNumber());
        statutory.setPassportNumber(req.getPassportNumber());
        statutory.setPassportExpiry(parseDate(req.getPassportExpiry()));
        statutory.setVoterId(req.getVoterId());
        statutory.setDrivingLicense(req.getDrivingLicense());
        statutory.setDrivingLicenseExpiry(parseDate(req.getDrivingLicenseExpiry()));
        statutory.setPfApplicable(req.getPfApplicable() != null ? req.getPfApplicable() : false);
        statutory.setEsiApplicable(req.getEsiApplicable() != null ? req.getEsiApplicable() : false);
        statutory.setTaxRegime(req.getTaxRegime());

        return statutoryRepository.save(statutory);
    }

    @Transactional(readOnly = true)
    public EmployeeStatutory getStatutory(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return statutoryRepository.findByEmployeeId(employeeId).orElse(null);
    }

    // ─────────────────────────────────────────────
    // Bank Account
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeBankAccount saveBankAccount(UUID employeeId, EmployeeBankAccountRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Upsert primary bank account (simple single-account approach)
        List<EmployeeBankAccount> existing = bankAccountRepository.findByEmployeeId(employeeId);
        EmployeeBankAccount account = existing.isEmpty()
                ? EmployeeBankAccount.builder().employee(employee).build()
                : existing.get(0);

        account.setBankName(req.getBankName());
        account.setBranchName(req.getBranchName());
        account.setAccountNumber(req.getAccountNumber());
        account.setIfscCode(req.getIfscCode());
        account.setAccountType(req.getAccountType());
        account.setAccountHolderName(req.getAccountHolderName());
        account.setPaymentMode(req.getPaymentMode());
        account.setPrimaryAccount(req.getPrimaryAccount() != null ? req.getPrimaryAccount() : true);

        return bankAccountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<EmployeeBankAccount> getBankAccounts(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return bankAccountRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Documents
    // ─────────────────────────────────────────────

    @Transactional
    public List<EmployeeDocument> saveDocuments(UUID employeeId, List<EmployeeDocumentRequest> requests) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Replace all documents with the new list
        documentRepository.deleteByEmployeeId(employeeId);

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<EmployeeDocument> docs = requests.stream()
                .map(req -> EmployeeDocument.builder()
                        .employee(employee)
                        .documentCategory(req.getDocumentCategory())
                        .documentName(req.getDocumentName())
                        .fileId(req.getFileId())
                        .expiryDate(parseDate(req.getExpiryDate()))
                        .build())
                .collect(Collectors.toList());

        return documentRepository.saveAll(docs);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getDocuments(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return documentRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Salary Revision
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeSalaryRevision addSalaryRevision(UUID employeeId, EmployeeSalaryRevisionRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        EmployeeSalaryRevision revision = EmployeeSalaryRevision.builder()
                .employee(employee)
                .revisionType(req.getRevisionType())
                .effectiveDate(parseDate(req.getEffectiveDate()))
                .annualCTC(req.getAnnualCTC())
                .incrementPercentage(req.getIncrementPercentage())
                .salaryComponents(req.getSalaryComponents())
                .reason(req.getReason())
                .build();

        return salaryRevisionRepository.save(revision);
    }

    @Transactional(readOnly = true)
    public List<EmployeeSalaryRevision> getSalaryRevisions(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return salaryRevisionRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    // ─────────────────────────────────────────────
    // Education
    // ─────────────────────────────────────────────

    @Transactional
    public List<EmployeeEducation> saveEducations(UUID employeeId, List<EmployeeEducationRequest> requests) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        educationRepository.deleteByEmployeeId(employeeId);

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<EmployeeEducation> educations = requests.stream()
                .map(req -> EmployeeEducation.builder()
                        .employee(employee)
                        .degree(req.getDegree())
                        .qualification(req.getQualification())
                        .institution(req.getInstitution())
                        .fieldOfStudy(req.getFieldOfStudy())
                        .startYear(req.getStartYear())
                        .endYear(req.getEndYear())
                        .grade(req.getGrade())
                        .build())
                .collect(Collectors.toList());

        return educationRepository.saveAll(educations);
    }

    @Transactional(readOnly = true)
    public List<EmployeeEducation> getEducations(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return educationRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Family / Nominee
    // ─────────────────────────────────────────────

    @Transactional
    public List<EmployeeFamily> saveFamilies(UUID employeeId, List<EmployeeFamilyRequest> requests) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        familyRepository.deleteByEmployeeId(employeeId);

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<EmployeeFamily> families = requests.stream()
                .map(req -> EmployeeFamily.builder()
                        .employee(employee)
                        .name(req.getName())
                        .relationship(req.getRelationship())
                        .dateOfBirth(parseDate(req.getDateOfBirth()))
                        .gender(req.getGender())
                        .phone(req.getPhone())
                        .dependent(req.getDependent() != null ? req.getDependent() : false)
                        .nominee(req.getNominee() != null ? req.getNominee() : false)
                        .nomineePercentage(req.getNomineePercentage())
                        .build())
                .collect(Collectors.toList());

        return familyRepository.saveAll(families);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getFamilies(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return familyRepository.findByEmployeeId(employeeId);
    }

    // ─────────────────────────────────────────────
    // Contract
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeContract saveContract(UUID employeeId, EmployeeContractRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        EmployeeContract contract = contractRepository
                .findByEmployeeId(employeeId)
                .orElse(EmployeeContract.builder().employee(employee).build());

        contract.setContractType(req.getContractType());
        contract.setStartDate(parseDate(req.getContractStartDate()));
        contract.setEndDate(parseDate(req.getContractEndDate()));
        contract.setAnnualCTC(req.getContractAnnualCTC());
        contract.setNoticePeriodDays(req.getContractNoticePeriod());
        contract.setContractTerms(req.getContractTerms());
        contract.setSignedContractFileId(req.getSignedContractFileId());

        return contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public EmployeeContract getContract(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return contractRepository.findByEmployeeId(employeeId).orElse(null);
    }

    // ─────────────────────────────────────────────
    // Position Change
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeePositionChange savePositionChange(UUID employeeId, EmployeePositionChangeRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        EmployeePositionChange change = EmployeePositionChange.builder()
                .employee(employee)
                .changeType(req.getPositionChangeType())
                .effectiveDate(parseDate(req.getPositionChangeEffectiveDate()))
                .departmentId(req.getPositionChangeDepartmentId())
                .designationId(req.getPositionChangeDesignationId())
                .grade(req.getPositionChangeGrade())
                .location(req.getPositionChangeLocation())
                .reportingManagerId(req.getPositionChangeReportingManagerId())
                .remarks(req.getPositionChangeRemarks())
                .build();

        return positionChangeRepository.save(change);
    }

    @Transactional(readOnly = true)
    public List<EmployeePositionChange> getPositionChanges(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return positionChangeRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    // ─────────────────────────────────────────────
    // Separation / Exit
    // ─────────────────────────────────────────────

    @Transactional
    public EmployeeSeparation saveSeparation(UUID employeeId, EmployeeSeparationRequest req) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        EmployeeSeparation separation = separationRepository
                .findByEmployeeId(employeeId)
                .orElse(EmployeeSeparation.builder().employee(employee).build());

        separation.setSeparationType(req.getSeparationType());
        separation.setResignationDate(parseDate(req.getResignationDate()));
        separation.setLastWorkingDate(parseDate(req.getLastWorkingDate()));
        separation.setNoticePeriodDays(req.getExitNoticePeriod());
        separation.setSeparationReason(req.getSeparationReason());
        separation.setExitInterview(req.getExitInterview() != null ? req.getExitInterview() : false);
        separation.setSeparationRemarks(req.getSeparationRemarks());

        return separationRepository.save(separation);
    }

    @Transactional(readOnly = true)
    public EmployeeSeparation getSeparation(UUID employeeId) {
        employeeService.getEmployeeById(employeeId);
        return separationRepository.findByEmployeeId(employeeId).orElse(null);
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    /**
     * Safely parses an ISO date string (YYYY-MM-DD). Returns null for blank/null input.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
