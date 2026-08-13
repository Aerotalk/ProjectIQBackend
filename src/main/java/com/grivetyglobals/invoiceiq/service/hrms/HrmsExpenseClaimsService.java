package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.*;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrmsExpenseClaimsService {

    private final ExpenseCategoryConfigRepository categoryRepository;
    private final ExpenseClaimTemplateRepository templateRepository;
    private final ExpenseClaimTemplateCategoryRepository templateCategoryRepository;
    private final ExpensePolicyRepository policyRepository;
    private final ReviewerAssignmentRepository reviewerAssignmentRepository;

    private final ExpenseClaimRepository claimRepository;
    private final ExpenseItemRepository itemRepository;
    private final ExpenseAdvanceRepository advanceRepository;
    private final ClaimBatchRepository batchRepository;
    private final ExpenseAuditLogRepository auditLogRepository;

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    private Organization getCurrentOrganization() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    private void logAudit(String entityType, UUID entityId, String status, String action, String comment) {
        ExpenseAuditLog log = ExpenseAuditLog.builder()
                .organization(getCurrentOrganization())
                .entityType(entityType)
                .entityId(entityId)
                .status(status)
                .action(action)
                .comment(comment)
                .date(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    // ─────────────────────────────────────────────────────────
    // CATEGORIES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpenseCategoryConfig> getCategories() {
        return categoryRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ExpenseCategoryConfig getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public ExpenseCategoryConfig createCategory(ExpenseCategoryConfig category) {
        category.setOrganization(getCurrentOrganization());
        if (category.getActive() == null) category.setActive(true);
        if (category.getReceiptRequired() == null) category.setReceiptRequired(false);
        return categoryRepository.save(category);
    }

    @Transactional
    public ExpenseCategoryConfig updateCategory(UUID id, ExpenseCategoryConfig updated) {
        ExpenseCategoryConfig cat = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        cat.setCategory(updated.getCategory());
        cat.setGlCode(updated.getGlCode());
        cat.setReceiptRequired(updated.getReceiptRequired());
        cat.setMinReceiptAmount(updated.getMinReceiptAmount());
        if (updated.getActive() != null) cat.setActive(updated.getActive());
        return categoryRepository.save(cat);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // TEMPLATES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpenseClaimTemplate> getTemplates() {
        return templateRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ExpenseClaimTemplate getTemplateById(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }

    @Transactional
    public ExpenseClaimTemplate createTemplate(ExpenseClaimTemplate template, List<UUID> allowedCategoryIds) {
        template.setOrganization(getCurrentOrganization());
        if (template.getActive() == null) template.setActive(true);
        ExpenseClaimTemplate saved = templateRepository.save(template);

        if (allowedCategoryIds != null) {
            for (UUID catId : allowedCategoryIds) {
                ExpenseCategoryConfig cat = categoryRepository.findById(catId).orElse(null);
                if (cat != null) {
                    templateCategoryRepository.save(ExpenseClaimTemplateCategory.builder()
                            .template(saved)
                            .category(cat)
                            .build());
                }
            }
        }
        return saved;
    }

    @Transactional
    public ExpenseClaimTemplate updateTemplate(UUID id, ExpenseClaimTemplate updated, List<UUID> allowedCategoryIds) {
        ExpenseClaimTemplate tmpl = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        tmpl.setTemplateName(updated.getTemplateName());
        tmpl.setDescription(updated.getDescription());
        if (updated.getActive() != null) tmpl.setActive(updated.getActive());
        ExpenseClaimTemplate saved = templateRepository.save(tmpl);

        if (allowedCategoryIds != null) {
            templateCategoryRepository.deleteByTemplateId(saved.getId());
            for (UUID catId : allowedCategoryIds) {
                ExpenseCategoryConfig cat = categoryRepository.findById(catId).orElse(null);
                if (cat != null) {
                    templateCategoryRepository.save(ExpenseClaimTemplateCategory.builder()
                            .template(saved)
                            .category(cat)
                            .build());
                }
            }
        }
        return saved;
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        templateCategoryRepository.deleteByTemplateId(id);
        templateRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // POLICIES & REVIEWER ASSIGNMENTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpensePolicy> getPolicies() {
        return policyRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public ExpensePolicy createPolicy(ExpensePolicy policy) {
        policy.setOrganization(getCurrentOrganization());
        return policyRepository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<ReviewerAssignment> getReviewerAssignments() {
        return reviewerAssignmentRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public ReviewerAssignment createReviewerAssignment(ReviewerAssignment assignment) {
        assignment.setOrganization(getCurrentOrganization());
        return reviewerAssignmentRepository.save(assignment);
    }

    // ─────────────────────────────────────────────────────────
    // CLAIMS & EXPENSE ITEMS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpenseClaim> getClaims() {
        return claimRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ExpenseClaim getClaimById(UUID id) {
        var claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
        if (!claim.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return claim;
    }

    @Transactional
    public ExpenseClaim createClaim(ExpenseClaim claim) {
        claim.setOrganization(getCurrentOrganization());
        if (claim.getStatus() == null) claim.setStatus("Draft");
        if (claim.getTotalClaimed() == null) claim.setTotalClaimed(BigDecimal.ZERO);
        if (claim.getApprovedAmount() == null) claim.setApprovedAmount(BigDecimal.ZERO);
        if (claim.getClaimNo() == null) {
            claim.setClaimNo("CLM-" + System.currentTimeMillis() % 100000);
        }
        ExpenseClaim saved = claimRepository.save(claim);
        logAudit("Claim", saved.getId(), saved.getStatus(), "Create", "Claim Envelope created");
        return saved;
    }

    @Transactional
    public ExpenseClaim createClaim(String title, String currency, String employeeId, String templateId) {
        ExpenseClaim claim = new ExpenseClaim();
        claim.setTitle(title);
        claim.setCurrency(currency);

        if (employeeId != null && !employeeId.isEmpty()) {
            Employee emp = employeeRepository.findById(UUID.fromString(employeeId))
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
            claim.setEmployee(emp);
        }

        if (templateId != null && !templateId.isEmpty()) {
            ExpenseClaimTemplate tmpl = templateRepository.findById(UUID.fromString(templateId))
                    .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
            claim.setTemplate(tmpl);
        }

        return createClaim(claim);
    }

    @Transactional
    public ExpenseClaim updateClaim(UUID id, ExpenseClaim updated) {
        ExpenseClaim claim = getClaimById(id);
        claim.setTitle(updated.getTitle());
        claim.setCurrency(updated.getCurrency());
        if (updated.getTemplate() != null) claim.setTemplate(updated.getTemplate());
        return claimRepository.save(claim);
    }

    @Transactional
    public ExpenseClaim submitClaim(UUID id) {
        ExpenseClaim claim = getClaimById(id);
        claim.setStatus("Submitted");
        claim.setSubmittedOn(LocalDateTime.now());
        ExpenseClaim saved = claimRepository.save(claim);
        logAudit("Claim", saved.getId(), "Submitted", "Submit", "Claim submitted for approval");
        return saved;
    }

    @Transactional
    public ExpenseClaim approveClaim(UUID id, BigDecimal approvedAmount, String comment) {
        ExpenseClaim claim = getClaimById(id);
        claim.setStatus("Approved");
        if (approvedAmount != null) {
            claim.setApprovedAmount(approvedAmount);
        } else {
            claim.setApprovedAmount(claim.getTotalClaimed());
        }
        ExpenseClaim saved = claimRepository.save(claim);
        logAudit("Claim", saved.getId(), "Approved", "Approve", comment != null ? comment : "Claim approved");
        return saved;
    }

    @Transactional
    public ExpenseClaim rejectClaim(UUID id, String comment) {
        ExpenseClaim claim = getClaimById(id);
        claim.setStatus("Rejected");
        ExpenseClaim saved = claimRepository.save(claim);
        logAudit("Claim", saved.getId(), "Rejected", "Reject", comment != null ? comment : "Claim rejected");
        return saved;
    }

    @Transactional
    public ExpenseClaim sendBackClaim(UUID id, String comment) {
        ExpenseClaim claim = getClaimById(id);
        claim.setStatus("Sent Back");
        ExpenseClaim saved = claimRepository.save(claim);
        logAudit("Claim", saved.getId(), "Sent Back", "Send Back", comment != null ? comment : "Sent back for corrections");
        return saved;
    }

    @Transactional
    public void deleteClaim(UUID id) {
        itemRepository.deleteByClaimId(id);
        claimRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ExpenseItem> getClaimItems(UUID claimId) {
        return itemRepository.findByClaimId(claimId);
    }

    @Transactional
    public ExpenseItem addExpenseItem(UUID claimId, ExpenseItem item) {
        ExpenseClaim claim = getClaimById(claimId);
        item.setClaim(claim);
        ExpenseItem saved = itemRepository.save(item);
        recalculateClaimTotal(claimId);
        return saved;
    }

    @Transactional
    public void deleteExpenseItem(UUID claimId, UUID itemId) {
        itemRepository.deleteById(itemId);
        recalculateClaimTotal(claimId);
    }

    private void recalculateClaimTotal(UUID claimId) {
        List<ExpenseItem> items = itemRepository.findByClaimId(claimId);
        BigDecimal total = items.stream()
                .map(ExpenseItem::getClaimAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ExpenseClaim claim = getClaimById(claimId);
        claim.setTotalClaimed(total);
        claimRepository.save(claim);
    }

    // ─────────────────────────────────────────────────────────
    // ADVANCES & BATCHES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpenseAdvance> getAdvances() {
        return advanceRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ExpenseAdvance getAdvanceById(UUID id) {
        return advanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advance not found"));
    }

    @Transactional
    public ExpenseAdvance createAdvance(ExpenseAdvance advance) {
        advance.setOrganization(getCurrentOrganization());
        if (advance.getStatus() == null) advance.setStatus("Pending");
        if (advance.getDisbursed() == null) advance.setDisbursed(false);
        if (advance.getOutstandingBalance() == null) advance.setOutstandingBalance(advance.getAmount());
        if (advance.getAdvanceNo() == null) {
            advance.setAdvanceNo("ADV-" + System.currentTimeMillis() % 10000);
        }
        ExpenseAdvance saved = advanceRepository.save(advance);
        logAudit("Advance", saved.getId(), saved.getStatus(), "Create", "Advance request created");
        return saved;
    }

    @Transactional
    public ExpenseAdvance updateAdvance(UUID id, ExpenseAdvance updated) {
        ExpenseAdvance advance = getAdvanceById(id);
        advance.setTripOrProject(updated.getTripOrProject());
        advance.setPurpose(updated.getPurpose());
        advance.setAmount(updated.getAmount());
        advance.setCurrency(updated.getCurrency());
        advance.setRequestedDate(updated.getRequestedDate());
        advance.setRequiredDate(updated.getRequiredDate());
        return advanceRepository.save(advance);
    }

    @Transactional
    public ExpenseAdvance approveAdvance(UUID id) {
        ExpenseAdvance advance = advanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Advance not found"));
        if (!advance.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        advance.setStatus("Approved");
        ExpenseAdvance saved = advanceRepository.save(advance);
        logAudit("Advance", saved.getId(), "Approved", "Approve", "Advance approved");
        return saved;
    }

    @Transactional
    public ExpenseAdvance disburseAdvance(UUID id) {
        ExpenseAdvance advance = advanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Advance not found"));
        if (!advance.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        advance.setStatus("Disbursed");
        advance.setDisbursed(true);
        ExpenseAdvance saved = advanceRepository.save(advance);
        logAudit("Advance", saved.getId(), "Disbursed", "Disburse", "Advance disbursed");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ClaimBatch> getBatches() {
        return batchRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ClaimBatch getBatchById(UUID id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    @Transactional
    public ClaimBatch createBatch(ClaimBatch batch) {
        batch.setOrganization(getCurrentOrganization());
        if (batch.getStatus() == null) batch.setStatus("Draft");
        if (batch.getBatchNo() == null) {
            batch.setBatchNo("BAT-" + System.currentTimeMillis() % 10000);
        }
        ClaimBatch saved = batchRepository.save(batch);
        logAudit("Batch", saved.getId(), saved.getStatus(), "Create", "Payment batch created");
        return saved;
    }

    @Transactional
    public ClaimBatch updateBatch(UUID id, ClaimBatch updated) {
        ClaimBatch batch = getBatchById(id);
        batch.setPayrollPeriod(updated.getPayrollPeriod());
        batch.setPaymentMethod(updated.getPaymentMethod());
        batch.setRemarks(updated.getRemarks());
        return batchRepository.save(batch);
    }

    @Transactional
    public ClaimBatch markBatchPaid(UUID id) {
        ClaimBatch batch = batchRepository.findById(id).orElseThrow(() -> new RuntimeException("Batch not found"));
        if (!batch.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        batch.setStatus("Paid");
        batch.setPaidOn(LocalDateTime.now());
        ClaimBatch saved = batchRepository.save(batch);
        logAudit("Batch", saved.getId(), "Paid", "Pay", "Batch marked as paid");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ExpenseAuditLog> getAuditLogs() {
        return auditLogRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }
}
