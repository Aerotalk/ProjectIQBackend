package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.service.hrms.HrmsExpenseClaimsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrms/expense-claims")
@RequiredArgsConstructor
public class HrmsExpenseClaimsController {

    private final HrmsExpenseClaimsService expenseClaimsService;

    // ─────────────────────────────────────────────────────────
    // CATEGORIES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategoryConfig>> getCategories() {
        return ResponseEntity.ok(expenseClaimsService.getCategories());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories/{id}")
    public ResponseEntity<ExpenseCategoryConfig> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getCategoryById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/categories")
    public ResponseEntity<ExpenseCategoryConfig> createCategory(@RequestBody ExpenseCategoryConfig category) {
        return ResponseEntity.ok(expenseClaimsService.createCategory(category));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/categories/{id}")
    public ResponseEntity<ExpenseCategoryConfig> updateCategory(@PathVariable UUID id, @RequestBody ExpenseCategoryConfig category) {
        return ResponseEntity.ok(expenseClaimsService.updateCategory(id, category));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        expenseClaimsService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // TEMPLATES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/templates")
    public ResponseEntity<List<ExpenseClaimTemplate>> getTemplates() {
        return ResponseEntity.ok(expenseClaimsService.getTemplates());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/templates/{id}")
    public ResponseEntity<ExpenseClaimTemplate> getTemplateById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getTemplateById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/templates")
    public ResponseEntity<ExpenseClaimTemplate> createTemplate(@RequestBody Map<String, Object> payload) {
        ExpenseClaimTemplate tmpl = ExpenseClaimTemplate.builder()
                .templateName((String) payload.get("templateName"))
                .description((String) payload.get("description"))
                .active(payload.get("active") != null ? (Boolean) payload.get("active") : true)
                .build();
        @SuppressWarnings("unchecked")
        List<String> catIdStrs = (List<String>) payload.get("allowedCategories");
        List<UUID> catIds = catIdStrs != null ? catIdStrs.stream().map(UUID::fromString).toList() : null;
        return ResponseEntity.ok(expenseClaimsService.createTemplate(tmpl, catIds));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/templates/{id}")
    public ResponseEntity<ExpenseClaimTemplate> updateTemplate(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        ExpenseClaimTemplate tmpl = ExpenseClaimTemplate.builder()
                .templateName((String) payload.get("templateName"))
                .description((String) payload.get("description"))
                .active(payload.get("active") != null ? (Boolean) payload.get("active") : true)
                .build();
        @SuppressWarnings("unchecked")
        List<String> catIdStrs = (List<String>) payload.get("allowedCategories");
        List<UUID> catIds = catIdStrs != null ? catIdStrs.stream().map(UUID::fromString).toList() : null;
        return ResponseEntity.ok(expenseClaimsService.updateTemplate(id, tmpl, catIds));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/templates/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        expenseClaimsService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // POLICIES & REVIEWER ASSIGNMENTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/policies")
    public ResponseEntity<List<ExpensePolicy>> getPolicies() {
        return ResponseEntity.ok(expenseClaimsService.getPolicies());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/policies")
    public ResponseEntity<ExpensePolicy> createPolicy(@RequestBody Map<String, Object> payload) {
        ExpenseCategoryConfig category = expenseClaimsService.getCategoryById(
                UUID.fromString((String) payload.get("categoryId")));

        ExpensePolicy policy = ExpensePolicy.builder()
                .category(category)
                .grade((String) payload.get("grade"))
                .limitType((String) payload.get("limitType"))
                .maxClaim(payload.get("maxClaim") != null
                        ? new BigDecimal(payload.get("maxClaim").toString()) : null)
                .periodLimit(payload.get("periodLimit") != null
                        ? new BigDecimal(payload.get("periodLimit").toString()) : null)
                .backdatedDays(payload.get("backdatedDays") != null
                        ? ((Number) payload.get("backdatedDays")).intValue() : null)
                .build();

        return ResponseEntity.ok(expenseClaimsService.createPolicy(policy));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reviewer-assignments")
    public ResponseEntity<List<ReviewerAssignment>> getReviewerAssignments() {
        return ResponseEntity.ok(expenseClaimsService.getReviewerAssignments());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reviewer-assignments")
    public ResponseEntity<ReviewerAssignment> createReviewerAssignment(@RequestBody ReviewerAssignment assignment) {
        return ResponseEntity.ok(expenseClaimsService.createReviewerAssignment(assignment));
    }

    // ─────────────────────────────────────────────────────────
    // CLAIMS & ITEMS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/claims")
    public ResponseEntity<List<ExpenseClaim>> getClaims() {
        return ResponseEntity.ok(expenseClaimsService.getClaims());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/claims/{id}")
    public ResponseEntity<ExpenseClaim> getClaimById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getClaimById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/claims")
    public ResponseEntity<ExpenseClaim> createClaim(@RequestBody Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String currency = (String) payload.get("currency");

        // Extract employee ID from either { employee: { id: "..." } } or { employee: "..." }
        String employeeId = null;
        Object empObj = payload.get("employee");
        if (empObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> empMap = (Map<String, Object>) empObj;
            employeeId = (String) empMap.get("id");
        } else if (empObj instanceof String) {
            employeeId = (String) empObj;
        }

        // Extract template ID from either { template: { id: "..." } } or { template: "..." }
        String templateId = null;
        Object tmplObj = payload.get("template");
        if (tmplObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tmplMap = (Map<String, Object>) tmplObj;
            templateId = (String) tmplMap.get("id");
        } else if (tmplObj instanceof String) {
            templateId = (String) tmplObj;
        }

        return ResponseEntity.ok(expenseClaimsService.createClaim(title, currency, employeeId, templateId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/claims/{id}")
    public ResponseEntity<ExpenseClaim> updateClaim(@PathVariable UUID id, @RequestBody ExpenseClaim claim) {
        return ResponseEntity.ok(expenseClaimsService.updateClaim(id, claim));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/claims/{id}/submit")
    public ResponseEntity<ExpenseClaim> submitClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.submitClaim(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/claims/{id}/approve")
    public ResponseEntity<ExpenseClaim> approveClaim(@PathVariable UUID id, @RequestBody(required = false) Map<String, Object> body) {
        BigDecimal amount = null;
        String comment = null;
        if (body != null) {
            if (body.get("approvedAmount") != null) {
                amount = new BigDecimal(body.get("approvedAmount").toString());
            }
            comment = (String) body.get("comment");
        }
        return ResponseEntity.ok(expenseClaimsService.approveClaim(id, amount, comment));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/claims/{id}/reject")
    public ResponseEntity<ExpenseClaim> rejectClaim(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(expenseClaimsService.rejectClaim(id, comment));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/claims/{id}/send-back")
    public ResponseEntity<ExpenseClaim> sendBackClaim(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(expenseClaimsService.sendBackClaim(id, comment));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/claims/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable UUID id) {
        expenseClaimsService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/claims/{id}/items")
    public ResponseEntity<List<ExpenseItem>> getClaimItems(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getClaimItems(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/claims/{id}/items")
    public ResponseEntity<ExpenseItem> addExpenseItem(@PathVariable UUID id, @RequestBody ExpenseItem item) {
        return ResponseEntity.ok(expenseClaimsService.addExpenseItem(id, item));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/claims/{claimId}/items/{itemId}")
    public ResponseEntity<Void> deleteExpenseItem(@PathVariable UUID claimId, @PathVariable UUID itemId) {
        expenseClaimsService.deleteExpenseItem(claimId, itemId);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // ADVANCES & BATCHES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/advances")
    public ResponseEntity<List<ExpenseAdvance>> getAdvances() {
        return ResponseEntity.ok(expenseClaimsService.getAdvances());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/advances/{id}")
    public ResponseEntity<ExpenseAdvance> getAdvanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getAdvanceById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/advances")
    public ResponseEntity<ExpenseAdvance> createAdvance(@RequestBody ExpenseAdvance advance) {
        return ResponseEntity.ok(expenseClaimsService.createAdvance(advance));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/advances/{id}")
    public ResponseEntity<ExpenseAdvance> updateAdvance(@PathVariable UUID id, @RequestBody ExpenseAdvance advance) {
        return ResponseEntity.ok(expenseClaimsService.updateAdvance(id, advance));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/advances/{id}/approve")
    public ResponseEntity<ExpenseAdvance> approveAdvance(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.approveAdvance(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/advances/{id}/disburse")
    public ResponseEntity<ExpenseAdvance> disburseAdvance(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.disburseAdvance(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/batches")
    public ResponseEntity<List<ClaimBatch>> getBatches() {
        return ResponseEntity.ok(expenseClaimsService.getBatches());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/batches/{id}")
    public ResponseEntity<ClaimBatch> getBatchById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.getBatchById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/batches")
    public ResponseEntity<ClaimBatch> createBatch(@RequestBody ClaimBatch batch) {
        return ResponseEntity.ok(expenseClaimsService.createBatch(batch));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/batches/{id}")
    public ResponseEntity<ClaimBatch> updateBatch(@PathVariable UUID id, @RequestBody ClaimBatch batch) {
        return ResponseEntity.ok(expenseClaimsService.updateBatch(id, batch));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/batches/{id}/pay")
    public ResponseEntity<ClaimBatch> markBatchPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimsService.markBatchPaid(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/audit-logs")
    public ResponseEntity<List<ExpenseAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(expenseClaimsService.getAuditLogs());
    }
}
