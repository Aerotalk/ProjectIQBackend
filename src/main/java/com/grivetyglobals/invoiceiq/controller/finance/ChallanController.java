package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ChallanDto;
import com.grivetyglobals.invoiceiq.service.finance.ChallanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/finance/challans")
@RequiredArgsConstructor
public class ChallanController {

    private final ChallanService challanService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ChallanDto>> getChallans(@RequestParam UUID companyId) {
        return ResponseEntity.ok(challanService.getChallansByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ChallanDto> getChallan(@PathVariable UUID id) {
        return ResponseEntity.ok(challanService.getChallan(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ChallanDto> createChallan(@RequestParam UUID companyId, @RequestBody ChallanDto dto) {
        return ResponseEntity.ok(challanService.createChallan(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ChallanDto> updateChallan(@PathVariable UUID id, @RequestBody ChallanDto dto) {
        return ResponseEntity.ok(challanService.updateChallan(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallan(@PathVariable UUID id) {
        challanService.deleteChallan(id);
        return ResponseEntity.noContent().build();
    }
}
