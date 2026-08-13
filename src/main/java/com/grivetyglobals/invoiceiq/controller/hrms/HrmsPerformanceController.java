package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.dto.hrms.performance.*;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.CalibrationRecord;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.Competency;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.RatingScale;
import com.grivetyglobals.invoiceiq.service.hrms.HrmsPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrms/performance")
@RequiredArgsConstructor
public class HrmsPerformanceController {

    private final HrmsPerformanceService performanceService;

    // ─────────────────────────────────────────────────────────
    // RATING SCALES & COMPETENCIES
    // ─────────────────────────────────────────────────────────

    @GetMapping("/rating-scales")
    public ResponseEntity<List<RatingScaleDTO>> getRatingScales() {
        return ResponseEntity.ok(performanceService.getRatingScales());
    }

    @PostMapping("/rating-scales")
    public ResponseEntity<RatingScaleDTO> createRatingScale(@RequestBody RatingScale ratingScale) {
        return ResponseEntity.ok(performanceService.createRatingScale(ratingScale));
    }

    @GetMapping("/competencies")
    public ResponseEntity<List<CompetencyDTO>> getCompetencies() {
        return ResponseEntity.ok(performanceService.getCompetencies());
    }

    @PostMapping("/competencies")
    public ResponseEntity<CompetencyDTO> createCompetency(@RequestBody Competency competency) {
        return ResponseEntity.ok(performanceService.createCompetency(competency));
    }

    @PutMapping("/competencies/{id}")
    public ResponseEntity<CompetencyDTO> updateCompetency(@PathVariable UUID id, @RequestBody Competency competency) {
        return ResponseEntity.ok(performanceService.updateCompetency(id, competency));
    }

    @DeleteMapping("/competencies/{id}")
    public ResponseEntity<Void> deleteCompetency(@PathVariable UUID id) {
        performanceService.deleteCompetency(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // APPRAISAL CYCLES
    // ─────────────────────────────────────────────────────────

    @GetMapping("/cycles")
    public ResponseEntity<List<AppraisalCycleDTO>> getCycles() {
        return ResponseEntity.ok(performanceService.getCycles());
    }

    @GetMapping("/cycles/{id}")
    public ResponseEntity<AppraisalCycleDTO> getCycleById(@PathVariable UUID id) {
        return ResponseEntity.ok(performanceService.getCycleById(id));
    }

    @PostMapping("/cycles")
    public ResponseEntity<AppraisalCycleDTO> createCycle(@Valid @RequestBody CreateCycleRequest request) {
        return ResponseEntity.ok(performanceService.createCycle(request));
    }

    @PutMapping("/cycles/{id}")
    public ResponseEntity<AppraisalCycleDTO> updateCycle(@PathVariable UUID id, @Valid @RequestBody CreateCycleRequest request) {
        return ResponseEntity.ok(performanceService.updateCycle(id, request));
    }

    @DeleteMapping("/cycles/{id}")
    public ResponseEntity<Void> deleteCycle(@PathVariable UUID id) {
        performanceService.deleteCycle(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // GOALS / KRAs
    // ─────────────────────────────────────────────────────────

    @GetMapping("/goals")
    public ResponseEntity<List<PerformanceGoalDTO>> getGoals() {
        return ResponseEntity.ok(performanceService.getGoals());
    }

    @GetMapping("/goals/{id}")
    public ResponseEntity<PerformanceGoalDTO> getGoalById(@PathVariable UUID id) {
        return ResponseEntity.ok(performanceService.getGoalById(id));
    }

    @PostMapping("/goals")
    public ResponseEntity<PerformanceGoalDTO> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        return ResponseEntity.ok(performanceService.createGoal(request));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<PerformanceGoalDTO> updateGoal(@PathVariable UUID id, @Valid @RequestBody CreateGoalRequest request) {
        return ResponseEntity.ok(performanceService.updateGoal(id, request));
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id) {
        performanceService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // SELF REVIEWS
    // ─────────────────────────────────────────────────────────

    @GetMapping("/reviews/self")
    public ResponseEntity<List<SelfReviewDTO>> getSelfReviews() {
        return ResponseEntity.ok(performanceService.getSelfReviews());
    }

    @GetMapping("/reviews/self/{id}")
    public ResponseEntity<SelfReviewDTO> getSelfReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(performanceService.getSelfReviewById(id));
    }

    @PostMapping("/reviews/self")
    public ResponseEntity<SelfReviewDTO> createSelfReview(
            @Valid @RequestBody SubmitReviewRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean submit) {
        return ResponseEntity.ok(performanceService.createOrUpdateSelfReview(request, submit, null));
    }

    @PutMapping("/reviews/self/{id}")
    public ResponseEntity<SelfReviewDTO> updateSelfReview(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitReviewRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean submit) {
        return ResponseEntity.ok(performanceService.createOrUpdateSelfReview(request, submit, id));
    }

    // ─────────────────────────────────────────────────────────
    // MANAGER REVIEWS
    // ─────────────────────────────────────────────────────────

    @GetMapping("/reviews/manager")
    public ResponseEntity<List<ManagerReviewDTO>> getManagerReviews() {
        return ResponseEntity.ok(performanceService.getManagerReviews());
    }

    @GetMapping("/reviews/manager/{id}")
    public ResponseEntity<ManagerReviewDTO> getManagerReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(performanceService.getManagerReviewById(id));
    }

    @PutMapping("/reviews/manager/{id}")
    public ResponseEntity<ManagerReviewDTO> updateManagerReview(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitReviewRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean submit) {
        return ResponseEntity.ok(performanceService.submitManagerReview(id, request, submit));
    }

    // ─────────────────────────────────────────────────────────
    // CALIBRATION
    // ─────────────────────────────────────────────────────────

    @GetMapping("/calibration")
    public ResponseEntity<List<CalibrationRecordDTO>> getCalibrationRecords() {
        return ResponseEntity.ok(performanceService.getCalibrationRecords());
    }

    @GetMapping("/calibration/cycle/{cycleId}")
    public ResponseEntity<List<CalibrationRecordDTO>> getCalibrationByCycle(@PathVariable UUID cycleId) {
        return ResponseEntity.ok(performanceService.getCalibrationByCycle(cycleId));
    }

    @PutMapping("/calibration/{id}")
    public ResponseEntity<CalibrationRecordDTO> updateCalibration(@PathVariable UUID id, @RequestBody CalibrationRecord calibrationRecord) {
        return ResponseEntity.ok(performanceService.updateCalibration(id, calibrationRecord));
    }

    @PostMapping("/calibration/{id}/finalize")
    public ResponseEntity<CalibrationRecordDTO> finalizeCalibration(@PathVariable UUID id) {
        return ResponseEntity.ok(performanceService.finalizeCalibration(id));
    }

    // ─────────────────────────────────────────────────────────
    // DASHBOARD & REPORTS
    // ─────────────────────────────────────────────────────────

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<PerformanceDashboardKpiDTO> getDashboardKPIs() {
        return ResponseEntity.ok(performanceService.getPerformanceDashboardKPIs());
    }

    @GetMapping("/reports/department-ratings")
    public ResponseEntity<List<DepartmentRatingDTO>> getDepartmentRatings(@RequestParam(required = false) UUID cycleId) {
        return ResponseEntity.ok(performanceService.getDepartmentRatings(cycleId));
    }

    @GetMapping("/reports/promotions")
    public ResponseEntity<List<Map<String, Object>>> getPromotionRecommendations(@RequestParam(required = false) UUID cycleId) {
        return ResponseEntity.ok(performanceService.getPromotionRecommendations(cycleId));
    }
}
