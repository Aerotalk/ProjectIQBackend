package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.*;
import com.grivetyglobals.invoiceiq.service.hrms.HrmsPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrms/performance")
@RequiredArgsConstructor
public class HrmsPerformanceController {

    private final HrmsPerformanceService performanceService;

    // ─────────────────────────────────────────────────────────
    // RATING SCALES & COMPETENCIES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rating-scales")
    public ResponseEntity<List<RatingScale>> getRatingScales() {
        return ResponseEntity.ok(performanceService.getRatingScales());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rating-scales")
    public ResponseEntity<RatingScale> createRatingScale(@RequestBody RatingScale ratingScale) {
        return ResponseEntity.ok(performanceService.createRatingScale(ratingScale));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/competencies")
    public ResponseEntity<List<Competency>> getCompetencies() {
        return ResponseEntity.ok(performanceService.getCompetencies());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/competencies")
    public ResponseEntity<Competency> createCompetency(@RequestBody Competency competency) {
        return ResponseEntity.ok(performanceService.createCompetency(competency));
    }

    // ─────────────────────────────────────────────────────────
    // APPRAISAL CYCLES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cycles")
    public ResponseEntity<List<AppraisalCycle>> getCycles() {
        return ResponseEntity.ok(performanceService.getCycles());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cycles")
    public ResponseEntity<AppraisalCycle> createCycle(@RequestBody AppraisalCycle cycle) {
        return ResponseEntity.ok(performanceService.createCycle(cycle));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/cycles/{id}")
    public ResponseEntity<AppraisalCycle> updateCycle(@PathVariable UUID id, @RequestBody AppraisalCycle cycle) {
        return ResponseEntity.ok(performanceService.updateCycle(id, cycle));
    }

    // ─────────────────────────────────────────────────────────
    // GOALS / KRAs
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/goals")
    public ResponseEntity<List<PerformanceGoal>> getGoals() {
        return ResponseEntity.ok(performanceService.getGoals());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goals")
    public ResponseEntity<PerformanceGoal> createGoal(@RequestBody PerformanceGoal goal) {
        return ResponseEntity.ok(performanceService.createGoal(goal));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/goals/{id}")
    public ResponseEntity<PerformanceGoal> updateGoal(@PathVariable UUID id, @RequestBody PerformanceGoal goal) {
        return ResponseEntity.ok(performanceService.updateGoal(id, goal));
    }

    // ─────────────────────────────────────────────────────────
    // REVIEWS & CALIBRATION
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reviews/self")
    public ResponseEntity<List<SelfReview>> getSelfReviews() {
        return ResponseEntity.ok(performanceService.getSelfReviews());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reviews/self")
    public ResponseEntity<SelfReview> saveSelfReview(@RequestBody SelfReview review, @RequestParam(defaultValue = "false") boolean submit) {
        return ResponseEntity.ok(performanceService.createOrSubmitSelfReview(review, submit));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reviews/manager")
    public ResponseEntity<List<ManagerReview>> getManagerReviews() {
        return ResponseEntity.ok(performanceService.getManagerReviews());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/reviews/manager/{id}")
    public ResponseEntity<ManagerReview> submitManagerReview(@PathVariable UUID id, @RequestBody ManagerReview review) {
        return ResponseEntity.ok(performanceService.submitManagerReview(id, review));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/calibration")
    public ResponseEntity<List<CalibrationRecord>> getCalibrationRecords() {
        return ResponseEntity.ok(performanceService.getCalibrationRecords());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/calibration/{id}")
    public ResponseEntity<CalibrationRecord> updateCalibration(@PathVariable UUID id, @RequestBody CalibrationRecord record) {
        return ResponseEntity.ok(performanceService.updateCalibration(id, record));
    }

    // ─────────────────────────────────────────────────────────
    // DASHBOARD & REPORTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard/kpis")
    public ResponseEntity<java.util.Map<String, Object>> getPerformanceDashboardKPIs() {
        return ResponseEntity.ok(performanceService.getPerformanceDashboardKPIs());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reports/department-ratings")
    public ResponseEntity<List<java.util.Map<String, Object>>> getDepartmentRatings() {
        return ResponseEntity.ok(performanceService.getDepartmentRatings());
    }
}
