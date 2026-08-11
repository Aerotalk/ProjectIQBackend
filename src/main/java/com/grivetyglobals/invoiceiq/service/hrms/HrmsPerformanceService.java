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
        if (!cycle.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
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
        if (!goal.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
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
        if (!review.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
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
        if (!rec.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        rec.setProposedRating(updated.getProposedRating());
        rec.setFinalRating(updated.getFinalRating());
        rec.setReviewer(updated.getReviewer());
        if (updated.getStatus() != null) rec.setStatus(updated.getStatus());
        return calibrationRecordRepository.save(rec);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getPerformanceDashboardKPIs() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        
        List<AppraisalCycle> cycles = cycleRepository.findByOrganizationId(orgId);
        long activeCycles = cycles.stream().filter(c -> "Active".equalsIgnoreCase(c.getStatus()) || "Review Phase".equalsIgnoreCase(c.getStatus())).count();

        List<SelfReview> selfReviews = selfReviewRepository.findByOrganizationId(orgId);
        long pendingSelf = selfReviews.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus()) || "Draft".equalsIgnoreCase(r.getStatus())).count();
        long completedSelf = selfReviews.stream().filter(r -> "Submitted".equalsIgnoreCase(r.getStatus())).count();

        List<ManagerReview> managerReviews = managerReviewRepository.findByOrganizationId(orgId);
        long pendingManager = managerReviews.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();
        long completedManager = managerReviews.stream().filter(r -> "Submitted".equalsIgnoreCase(r.getStatus()) || "Finalized".equalsIgnoreCase(r.getStatus())).count();

        java.util.Map<String, Object> kpis = new java.util.HashMap<>();
        kpis.put("activeCycles", activeCycles);
        kpis.put("pendingSelf", pendingSelf);
        kpis.put("pendingManager", pendingManager);
        kpis.put("completedReviews", completedSelf + completedManager);
        kpis.put("averageRating", 4.1);

        // topGoals mock
        List<java.util.Map<String, Object>> topGoals = new java.util.ArrayList<>();
        kpis.put("topGoals", topGoals); // The frontend handles array maps

        // cycleStatuses
        List<java.util.Map<String, Object>> cycleStatuses = new java.util.ArrayList<>();
        java.util.Map<String, Object> c1 = new java.util.HashMap<>(); c1.put("name", "Q1 Review"); c1.put("value", 80);
        java.util.Map<String, Object> c2 = new java.util.HashMap<>(); c2.put("name", "Q2 Review"); c2.put("value", 45);
        cycleStatuses.add(c1); cycleStatuses.add(c2);
        kpis.put("cycleStatuses", cycleStatuses);

        // departmentRatings
        kpis.put("departmentRatings", getDepartmentRatings());

        return kpis;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<java.util.Map<String, Object>> getDepartmentRatings() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        List<PerformanceGoal> allGoals = goalRepository.findByOrganizationId(orgId);
        
        java.util.Map<String, java.util.Map<String, Object>> deptMap = new java.util.HashMap<>();
        
        for (PerformanceGoal g : allGoals) {
            String deptName = (g.getEmployee() != null && g.getEmployee().getDepartment() != null) 
                    ? g.getEmployee().getDepartment().getDepartmentName() : "Unknown";
            
            deptMap.putIfAbsent(deptName, new java.util.HashMap<>());
            java.util.Map<String, Object> data = deptMap.get(deptName);
            
            data.putIfAbsent("name", deptName);
            data.putIfAbsent("totalRating", 0.0);
            data.putIfAbsent("topPerformers", 0);
            data.putIfAbsent("needsImprovement", 0);
            
            java.util.Set<UUID> empSet = (java.util.Set<UUID>) data.getOrDefault("employees", new java.util.HashSet<UUID>());
            if (g.getEmployee() != null) {
                empSet.add(g.getEmployee().getId());
            }
            data.put("employees", empSet);
            
            double rating = g.getProgress() != null ? g.getProgress() / 20.0 : 3.5;
            double currentTotal = (double) data.get("totalRating");
            data.put("totalRating", currentTotal + rating);
            
            if ("Completed".equals(g.getStatus()) || (g.getProgress() != null && g.getProgress() >= 100)) {
                data.put("topPerformers", (int) data.get("topPerformers") + 1);
            } else if (g.getProgress() != null && g.getProgress() < 50 && !"Draft".equals(g.getStatus())) {
                data.put("needsImprovement", (int) data.get("needsImprovement") + 1);
            }
        }
        
        List<java.util.Map<String, Object>> results = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> data : deptMap.values()) {
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("department", data.get("name"));
            result.put("name", data.get("name"));
            
            int top = (int) data.get("topPerformers");
            int needs = (int) data.get("needsImprovement");
            java.util.Set<UUID> empSet = (java.util.Set<UUID>) data.get("employees");
            int totalEmps = empSet.size();
            
            double totalRating = (double) data.get("totalRating");
            int countForAvg = Math.max(1, top + needs + totalEmps);
            
            result.put("avgRating", totalRating / countForAvg);
            result.put("rating", totalRating / countForAvg);
            result.put("topPerformers", top);
            result.put("needsImprovement", needs);
            result.put("totalEmployees", totalEmps);
            
            results.add(result);
        }
        
        return results;
    }
}
