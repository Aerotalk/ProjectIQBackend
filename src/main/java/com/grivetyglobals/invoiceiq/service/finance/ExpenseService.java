package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ExpenseDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.Expense;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ExpenseRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;

    public List<ExpenseDto> getExpensesByCompany(UUID companyId) {
        return expenseRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ExpenseDto getExpense(UUID id) {
        return mapToDto(expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found")));
    }

    @Transactional
    public ExpenseDto createExpense(UUID companyId, ExpenseDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Expense expense = new Expense();
        expense.setCompany(company);
        mapToEntity(dto, expense);

        return mapToDto(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseDto updateExpense(UUID id, ExpenseDto dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        mapToEntity(dto, expense);

        return mapToDto(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        expenseRepository.deleteById(id);
    }

    private void mapToEntity(ExpenseDto dto, Expense expense) {
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            expense.setProject(project);
        } else {
            expense.setProject(null);
        }

        expense.setExpenseType(dto.getExpenseType());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setAmount(dto.getAmount());
        expense.setRemarks(dto.getRemarks());
        expense.setStatus(dto.getStatus());
        expense.setAttachmentFileId(dto.getAttachmentFileId());
    }

    private ExpenseDto mapToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        
        if (expense.getProject() != null) {
            dto.setProjectId(expense.getProject().getId());
            dto.setProjectName(expense.getProject().getProjectName());
        }

        dto.setExpenseType(expense.getExpenseType());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setAmount(expense.getAmount());
        dto.setRemarks(expense.getRemarks());
        dto.setStatus(expense.getStatus());
        dto.setAttachmentFileId(expense.getAttachmentFileId());
        
        return dto;
    }
}
