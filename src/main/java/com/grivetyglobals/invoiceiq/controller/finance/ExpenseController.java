package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ExpenseDto;
import com.grivetyglobals.invoiceiq.service.finance.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing project expenses in the finance module.
 * Provides endpoints for creating, reading, updating, and deleting expenses.
 */
@RestController
@RequestMapping("/api/admin/projects/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Retrieves all expenses for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of ExpenseDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(@RequestParam UUID companyId) {
        return ResponseEntity.ok(expenseService.getExpensesByCompany(companyId));
    }

    /**
     * Retrieves a specific expense by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the expense
     * @return the ExpenseDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpense(id));
    }

    /**
     * Creates a new expense for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the expense data payload
     * @return the created ExpenseDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@RequestParam UUID companyId, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.createExpense(companyId, dto));
    }

    /**
     * Updates an existing expense.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the expense to update
     * @param dto the updated expense data payload
     * @return the updated ExpenseDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable UUID id, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto));
    }

    /**
     * Deletes an expense by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the expense to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
