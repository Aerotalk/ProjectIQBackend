package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.*;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.performance.*;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrmsPerformanceService {

    private final RatingScaleRepository ratingScaleRepository;
    private final CompetencyRepository competencyRepository;
    private final AppraisalCycleRepository cycleRepository;
    private final PerformanceGoalRepository goalRepository;
    private final SelfReviewRepository selfReviewRepository;
    private final ManagerReviewRepository managerReviewRepository;
    private final CalibrationRecordRepository calibrationRecordRepository;

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;

    private Organization getCurrentOrganization() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    // ─────────────────────────────────────────────────────────
    // RATING SCALES & COMPETENCIES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RatingScale> getRatingScales() {
        return ratingScaleRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public RatingScale createRatingScale(RatingScale ratingScale) {
        ratingScale.setOrganization(getCurrentOrganization());
        if (ratingScale.getLevels() != null) {
            ratingScale.getLevels().forEach(lvl -> lvl.setRatingScale(ratingScale));
        }
        return ratingScaleRepository.save(ratingScale);
    }

    @Transactional(readOnly = true)
    public List<Competency> getCompetencies() {
        return competencyRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public Competency createCompetency(Competency competency) {
        competency.setOrganization(getCurrentOrganization());
        if (competency.getActive() == null) competency.setActive(true);
        return competencyRepository.save(competency);
    }

    // ─────────────────────────────────────────────────────────
    // APPRAISAL CYCLES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AppraisalCycle> getCycles() {
        return cycleRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public AppraisalCycle createCycle(AppraisalCycle cycle) {
        cycle.setOrganization(getCurrentOrganization());
        if (cycle.getStatus() == null) cycle.setStatus("Draft");
        if (cycle.getEligibleCount() == null) cycle.setEligibleCount(0);
        if (cycle.getCompletionPercentage() == null) cycle.setCompletionPercentage(0);
        return cycleRepository.save(cycle);
    }

    @Transactional
    public AppraisalCycle updateCycle(UUID id, AppraisalCycle updated) {
        AppraisalCycle cycle = cycleRepository.findById(id).orElseThrow(() -> new RuntimeException("Cycle not found"));
        cycle.setName(updated.getName());
        cycle.setType(updated.getType());
        cycle.setPeriod(updated.getPeriod());
        cycle.setStartDate(updated.getStartDate());
        cycle.setEndDate(updated.getEndDate());
        cycle.setSelfReviewDeadline(updated.getSelfReviewDeadline());
        cycle.setManagerReviewDeadline(updated.getManagerReviewDeadline());
        cycle.setHrReviewDeadline(updated.getHrReviewDeadline());
        if (updated.getStatus() != null) cycle.setStatus(updated.getStatus());
        if (updated.getDescription() != null) cycle.setDescription(updated.getDescription());
        return cycleRepository.save(cycle);
    }

    // ─────────────────────────────────────────────────────────
    // GOALS / KRAs
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PerformanceGoal> getGoals() {
        return goalRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public PerformanceGoal createGoal(PerformanceGoal goal) {
        goal.setOrganization(getCurrentOrganization());
        if (goal.getStatus() == null) goal.setStatus("In Progress");
        calculateGoalProgress(goal);
        return goalRepository.save(goal);
    }

    @Transactional
    public PerformanceGoal updateGoal(UUID id, PerformanceGoal updated) {
        PerformanceGoal goal = goalRepository.findById(id).orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setTitle(updated.getTitle());
        goal.setDescription(updated.getDescription());
        goal.setCategory(updated.getCategory());
        goal.setWeightage(updated.getWeightage());
        goal.setKpi(updated.getKpi());
        goal.setTargetValue(updated.getTargetValue());
        goal.setCurrentValue(updated.getCurrentValue());
        goal.setUnit(updated.getUnit());
        goal.setDueDate(updated.getDueDate());
        goal.setPriority(updated.getPriority());
        if (updated.getStatus() != null) goal.setStatus(updated.getStatus());
        calculateGoalProgress(goal);
        return goalRepository.save(goal);
    }

    private void calculateGoalProgress(PerformanceGoal goal) {
        if (goal.getTargetValue() != null && goal.getCurrentValue() != null && goal.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = goal.getCurrentValue()
                    .multiply(new BigDecimal(100))
                    .divide(goal.getTargetValue(), 0, RoundingMode.HALF_UP);
            goal.setProgress(Math.min(100, percentage.intValue()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // SELF REVIEWS & AUTOMATIC MANAGER LINKING
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SelfReview> getSelfReviews() {
        return selfReviewRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public SelfReview createOrSubmitSelfReview(SelfReview review, boolean submit) {
        review.setOrganization(getCurrentOrganization());
        if (submit) {
            review.setStatus("Submitted");
            review.setSubmittedOn(LocalDateTime.now());
        } else {
            review.setStatus("Draft");
        }

        if (review.getGoalRatings() != null) {
            review.getGoalRatings().forEach(gr -> gr.setSelfReview(review));
        }
        if (review.getCompetencyRatings() != null) {
            review.getCompetencyRatings().forEach(cr -> cr.setSelfReview(review));
        }

        SelfReview savedSelfReview = selfReviewRepository.save(review);

        // Automatic Manager Review Link creation upon submission
        if (submit) {
            Employee employee = employeeRepository.findById(savedSelfReview.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            Employee reportingManager = employee.getReportingManager();
            if (reportingManager != null) {
                ManagerReview existingManagerReview = managerReviewRepository.findBySelfReviewId(savedSelfReview.getId()).orElse(null);
                if (existingManagerReview == null) {
                    ManagerReview managerReview = ManagerReview.builder()
                            .organization(getCurrentOrganization())
                            .selfReview(savedSelfReview)
                            .employee(employee)
                            .manager(reportingManager)
                            .cycle(savedSelfReview.getCycle())
                            .status("Pending")
                            .overallRating(BigDecimal.ZERO)
                            .build();
                    managerReviewRepository.save(managerReview);
                }
            }
        }

        return savedSelfReview;
    }

    // ─────────────────────────────────────────────────────────
    // MANAGER REVIEWS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ManagerReview> getManagerReviews() {
        return managerReviewRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public ManagerReview submitManagerReview(UUID id, ManagerReview updated) {
        ManagerReview review = managerReviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Manager Review not found"));
        review.setPromotionRecommendation(updated.getPromotionRecommendation());
        review.setTrainingRecommendation(updated.getTrainingRecommendation());
        review.setImprovementPlan(updated.getImprovementPlan());
        review.setOverallRating(updated.getOverallRating());
        review.setManagerComments(updated.getManagerComments());
        review.setStatus("Completed");
        review.setCompletedOn(LocalDateTime.now());
        return managerReviewRepository.save(review);
    }

    // ─────────────────────────────────────────────────────────
    // CALIBRATION RECORDS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CalibrationRecord> getCalibrationRecords() {
        return calibrationRecordRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public CalibrationRecord updateCalibration(UUID id, CalibrationRecord updated) {
        CalibrationRecord rec = calibrationRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Calibration record not found"));
        rec.setProposedRating(updated.getProposedRating());
        rec.setFinalRating(updated.getFinalRating());
        rec.setReviewer(updated.getReviewer());
        if (updated.getStatus() != null) rec.setStatus(updated.getStatus());
        return calibrationRecordRepository.save(rec);
    }
}
