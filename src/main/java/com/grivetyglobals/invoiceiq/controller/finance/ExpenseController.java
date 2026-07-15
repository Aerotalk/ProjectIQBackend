package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ExpenseDto;
import com.grivetyglobals.invoiceiq.service.finance.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/projects/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(@RequestParam UUID companyId) {
        return ResponseEntity.ok(expenseService.getExpensesByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpense(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@RequestParam UUID companyId, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.createExpense(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable UUID id, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
