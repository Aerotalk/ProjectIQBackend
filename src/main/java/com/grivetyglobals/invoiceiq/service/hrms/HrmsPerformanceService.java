package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.dto.hrms.performance.*;
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
import java.util.*;
import java.util.stream.Collectors;

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
    public List<RatingScaleDTO> getRatingScales() {
        return ratingScaleRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream().map(RatingScaleDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public RatingScaleDTO createRatingScale(RatingScale ratingScale) {
        ratingScale.setOrganization(getCurrentOrganization());
        if (ratingScale.getLevels() != null) {
            ratingScale.getLevels().forEach(lvl -> lvl.setRatingScale(ratingScale));
        }
        return RatingScaleDTO.fromEntity(ratingScaleRepository.save(ratingScale));
    }

    @Transactional(readOnly = true)
    public List<CompetencyDTO> getCompetencies() {
        return competencyRepository.findByOrganizationIdAndActiveTrue(SecurityUtils.getCurrentOrganizationId())
                .stream().map(CompetencyDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public CompetencyDTO createCompetency(Competency competency) {
        competency.setOrganization(getCurrentOrganization());
        if (competency.getActive() == null) competency.setActive(true);
        return CompetencyDTO.fromEntity(competencyRepository.save(competency));
    }

    @Transactional
    public CompetencyDTO updateCompetency(UUID id, Competency updated) {
        Competency comp = competencyRepository.findById(id).orElseThrow(() -> new RuntimeException("Competency not found"));
        if (!comp.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        comp.setName(updated.getName());
        comp.setDescription(updated.getDescription());
        comp.setCategory(updated.getCategory());
        comp.setWeightage(updated.getWeightage());
        return CompetencyDTO.fromEntity(competencyRepository.save(comp));
    }

    @Transactional
    public void deleteCompetency(UUID id) {
        Competency comp = competencyRepository.findById(id).orElseThrow(() -> new RuntimeException("Competency not found"));
        if (!comp.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        comp.setActive(false);
        competencyRepository.save(comp);
    }

    // ─────────────────────────────────────────────────────────
    // APPRAISAL CYCLES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AppraisalCycleDTO> getCycles() {
        return cycleRepository.findByOrganizationIdOrderByCreatedAtDesc(SecurityUtils.getCurrentOrganizationId())
                .stream().map(AppraisalCycleDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppraisalCycleDTO getCycleById(UUID id) {
        AppraisalCycle cycle = cycleRepository.findById(id).orElseThrow(() -> new RuntimeException("Cycle not found"));
        if (!cycle.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return AppraisalCycleDTO.fromEntity(cycle);
    }

    @Transactional
    public AppraisalCycleDTO createCycle(CreateCycleRequest req) {
        if (req.getStartDate() != null && req.getEndDate() != null && req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        AppraisalCycle cycle = AppraisalCycle.builder()
                .organization(getCurrentOrganization())
                .name(req.getName())
                .type(req.getType())
                .period(req.getPeriod())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .selfReviewDeadline(req.getSelfReviewDeadline())
                .managerReviewDeadline(req.getManagerReviewDeadline())
                .hrReviewDeadline(req.getHrReviewDeadline())
                .status(req.getStatus() != null ? req.getStatus() : "Draft")
                .description(req.getDescription())
                .eligibleCount(0)
                .completionPercentage(0)
                .build();
                
        return AppraisalCycleDTO.fromEntity(cycleRepository.save(cycle));
    }

    @Transactional
    public AppraisalCycleDTO updateCycle(UUID id, CreateCycleRequest updated) {
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
        return AppraisalCycleDTO.fromEntity(cycleRepository.save(cycle));
    }

    @Transactional
    public void deleteCycle(UUID id) {
        AppraisalCycle cycle = cycleRepository.findById(id).orElseThrow(() -> new RuntimeException("Cycle not found"));
        if (!cycle.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        cycleRepository.delete(cycle);
    }

    // ─────────────────────────────────────────────────────────
    // GOALS / KRAs
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PerformanceGoalDTO> getGoals() {
        return goalRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream().map(PerformanceGoalDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerformanceGoalDTO getGoalById(UUID id) {
        PerformanceGoal goal = goalRepository.findById(id).orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return PerformanceGoalDTO.fromEntity(goal);
    }

    @Transactional
    public PerformanceGoalDTO createGoal(CreateGoalRequest req) {
        Employee employee = employeeRepository.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
                
        AppraisalCycle cycle = null;
        if (req.getCycleId() != null) {
            cycle = cycleRepository.findById(req.getCycleId())
                    .orElseThrow(() -> new RuntimeException("Cycle not found"));
        }
        
        PerformanceGoal goal = PerformanceGoal.builder()
                .organization(getCurrentOrganization())
                .title(req.getTitle())
                .description(req.getDescription())
                .employee(employee)
                .cycle(cycle)
                .category(req.getCategory())
                .weightage(req.getWeightage())
                .kpi(req.getKpi())
                .targetValue(req.getTargetValue())
                .currentValue(req.getCurrentValue())
                .unit(req.getUnit())
                .dueDate(req.getDueDate())
                .priority(req.getPriority())
                .status(req.getStatus() != null ? req.getStatus() : "In Progress")
                .comments(req.getComments())
                .build();
                
        calculateGoalProgress(goal);
        return PerformanceGoalDTO.fromEntity(goalRepository.save(goal));
    }

    @Transactional
    public PerformanceGoalDTO updateGoal(UUID id, CreateGoalRequest updated) {
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
        goal.setComments(updated.getComments());
        
        calculateGoalProgress(goal);
        return PerformanceGoalDTO.fromEntity(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(UUID id) {
        PerformanceGoal goal = goalRepository.findById(id).orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        goal.setStatus("Cancelled");
        goalRepository.save(goal);
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
    // SELF REVIEWS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SelfReviewDTO> getSelfReviews() {
        return selfReviewRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream().map(SelfReviewDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SelfReviewDTO getSelfReviewById(UUID id) {
        SelfReview review = selfReviewRepository.findByIdAndOrganizationId(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Self Review not found"));
        return SelfReviewDTO.fromEntity(review);
    }

    @Transactional
    public SelfReviewDTO createOrUpdateSelfReview(SubmitReviewRequest req, boolean submit, UUID id) {
        Organization org = getCurrentOrganization();
        
        SelfReview review;
        if (id != null) {
            review = selfReviewRepository.findByIdAndOrganizationId(id, org.getId())
                    .orElseThrow(() -> new RuntimeException("Review not found"));
        } else {
            // Check if one already exists for this employee and cycle
            Optional<SelfReview> existing = selfReviewRepository.findByEmployeeIdAndCycleId(req.getEmployeeId(), req.getCycleId());
            if (existing.isPresent()) {
                review = existing.get();
            } else {
                Employee employee = employeeRepository.findById(req.getEmployeeId())
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                AppraisalCycle cycle = cycleRepository.findById(req.getCycleId())
                        .orElseThrow(() -> new RuntimeException("Cycle not found"));
                
                review = SelfReview.builder()
                        .organization(org)
                        .employee(employee)
                        .cycle(cycle)
                        .goalRatings(new ArrayList<>())
                        .competencyRatings(new ArrayList<>())
                        .build();
            }
        }

        review.setStrengths(req.getStrengths());
        review.setAreasOfImprovement(req.getAreasOfImprovement());
        review.setOverallRating(req.getOverallRating());
        
        if (submit) {
            review.setStatus("Submitted");
            review.setSubmittedOn(LocalDateTime.now());
        } else {
            review.setStatus("Draft");
        }

        // Process Goal Ratings
        if (req.getGoalAchievement() != null) {
            review.getGoalRatings().clear();
            for (SubmitReviewRequest.GoalRatingSubmitDTO gReq : req.getGoalAchievement()) {
                PerformanceGoal goal = goalRepository.findById(gReq.getGoalId()).orElse(null);
                if (goal != null) {
                    SelfReviewGoalRating gr = SelfReviewGoalRating.builder()
                            .selfReview(review)
                            .goal(goal)
                            .employeeRating(gReq.getRating())
                            .employeeComment(gReq.getComments())
                            .build();
                    review.getGoalRatings().add(gr);
                }
            }
        }

        // Process Competency Ratings
        if (req.getCompetencyRatings() != null) {
            review.getCompetencyRatings().clear();
            for (SubmitReviewRequest.CompetencyRatingSubmitDTO cReq : req.getCompetencyRatings()) {
                Competency comp = competencyRepository.findById(cReq.getCompetencyId()).orElse(null);
                if (comp != null) {
                    SelfReviewCompetencyRating cr = SelfReviewCompetencyRating.builder()
                            .selfReview(review)
                            .competency(comp)
                            .employeeRating(cReq.getRating())
                            .employeeComment(cReq.getComments())
                            .build();
                    review.getCompetencyRatings().add(cr);
                }
            }
        }

        SelfReview savedSelfReview = selfReviewRepository.save(review);

        // Automatic Manager Review Link creation upon submission
        if (submit) {
            Employee reportingManager = savedSelfReview.getEmployee().getReportingManager();
            if (reportingManager != null) {
                ManagerReview existingManagerReview = managerReviewRepository.findBySelfReviewId(savedSelfReview.getId()).orElse(null);
                if (existingManagerReview == null) {
                    ManagerReview managerReview = ManagerReview.builder()
                            .organization(org)
                            .selfReview(savedSelfReview)
                            .employee(savedSelfReview.getEmployee())
                            .manager(reportingManager)
                            .cycle(savedSelfReview.getCycle())
                            .status("Pending")
                            .overallRating(BigDecimal.ZERO)
                            .goalRatings(new ArrayList<>())
                            .competencyRatings(new ArrayList<>())
                            .build();
                    managerReviewRepository.save(managerReview);
                }
            }
        }

        return SelfReviewDTO.fromEntity(savedSelfReview);
    }

    // ─────────────────────────────────────────────────────────
    // MANAGER REVIEWS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ManagerReviewDTO> getManagerReviews() {
        return managerReviewRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream().map(ManagerReviewDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ManagerReviewDTO getManagerReviewById(UUID id) {
        ManagerReview review = managerReviewRepository.findByIdAndOrganizationId(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Manager Review not found"));
        return ManagerReviewDTO.fromEntity(review);
    }

    @Transactional
    public ManagerReviewDTO submitManagerReview(UUID id, SubmitReviewRequest req, boolean submit) {
        ManagerReview review = managerReviewRepository.findByIdAndOrganizationId(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Manager Review not found"));
        
        review.setPromotionRecommendation(req.getPromotionRecommendation());
        review.setTrainingRecommendation(req.getTrainingRecommendation());
        review.setImprovementPlan(req.getImprovementPlan());
        review.setOverallRating(req.getOverallRating());
        review.setManagerComments(req.getManagerComments());
        
        if (submit) {
            review.setStatus("Completed");
            review.setCompletedOn(LocalDateTime.now());
        }

        // Process Goal Ratings
        if (req.getGoalAchievement() != null) {
            review.getGoalRatings().clear();
            for (SubmitReviewRequest.GoalRatingSubmitDTO gReq : req.getGoalAchievement()) {
                PerformanceGoal goal = goalRepository.findById(gReq.getGoalId()).orElse(null);
                if (goal != null) {
                    ManagerReviewGoalRating gr = ManagerReviewGoalRating.builder()
                            .managerReview(review)
                            .goal(goal)
                            .managerRating(gReq.getRating())
                            .managerComment(gReq.getComments())
                            .build();
                    review.getGoalRatings().add(gr);
                }
            }
        }

        // Process Competency Ratings
        if (req.getCompetencyRatings() != null) {
            review.getCompetencyRatings().clear();
            for (SubmitReviewRequest.CompetencyRatingSubmitDTO cReq : req.getCompetencyRatings()) {
                Competency comp = competencyRepository.findById(cReq.getCompetencyId()).orElse(null);
                if (comp != null) {
                    ManagerReviewCompetencyRating cr = ManagerReviewCompetencyRating.builder()
                            .managerReview(review)
                            .competency(comp)
                            .managerRating(cReq.getRating())
                            .managerComment(cReq.getComments())
                            .build();
                    review.getCompetencyRatings().add(cr);
                }
            }
        }

        ManagerReview saved = managerReviewRepository.save(review);
        
        if (submit) {
            // Auto create calibration record
            CalibrationRecord existingCal = calibrationRecordRepository.findByOrganizationIdAndCycleId(review.getOrganization().getId(), review.getCycle().getId())
                    .stream().filter(c -> c.getEmployee().getId().equals(review.getEmployee().getId()))
                    .findFirst().orElse(null);
                    
            if (existingCal == null) {
                CalibrationRecord cal = CalibrationRecord.builder()
                        .organization(review.getOrganization())
                        .employee(review.getEmployee())
                        .cycle(review.getCycle())
                        .currentRating(review.getOverallRating())
                        .status("Pending")
                        .build();
                calibrationRecordRepository.save(cal);
            } else {
                existingCal.setCurrentRating(review.getOverallRating());
                calibrationRecordRepository.save(existingCal);
            }
        }
        
        return ManagerReviewDTO.fromEntity(saved);
    }

    // ─────────────────────────────────────────────────────────
    // CALIBRATION RECORDS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CalibrationRecordDTO> getCalibrationRecords() {
        return calibrationRecordRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream().map(CalibrationRecordDTO::fromEntity).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CalibrationRecordDTO> getCalibrationByCycle(UUID cycleId) {
        return calibrationRecordRepository.findByOrganizationIdAndCycleId(SecurityUtils.getCurrentOrganizationId(), cycleId)
                .stream().map(CalibrationRecordDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public CalibrationRecordDTO updateCalibration(UUID id, CalibrationRecord updated) {
        CalibrationRecord rec = calibrationRecordRepository.findByIdAndOrganizationId(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Calibration record not found"));
        
        rec.setProposedRating(updated.getProposedRating());
        rec.setFinalRating(updated.getFinalRating());
        rec.setReviewer(updated.getReviewer());
        if (updated.getStatus() != null) rec.setStatus(updated.getStatus());
        return CalibrationRecordDTO.fromEntity(calibrationRecordRepository.save(rec));
    }
    
    @Transactional
    public CalibrationRecordDTO finalizeCalibration(UUID id) {
        CalibrationRecord rec = calibrationRecordRepository.findByIdAndOrganizationId(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Calibration record not found"));
        
        rec.setStatus("Finalized");
        if (rec.getFinalRating() == null && rec.getProposedRating() != null) {
            rec.setFinalRating(rec.getProposedRating());
        } else if (rec.getFinalRating() == null) {
            rec.setFinalRating(rec.getCurrentRating());
        }
        
        return CalibrationRecordDTO.fromEntity(calibrationRecordRepository.save(rec));
    }

    // ─────────────────────────────────────────────────────────
    // DASHBOARD & REPORTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PerformanceDashboardKpiDTO getPerformanceDashboardKPIs() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        
        long activeCycles = cycleRepository.findByOrganizationIdAndStatus(orgId, "Active").size() + 
                            cycleRepository.findByOrganizationIdAndStatus(orgId, "Review Phase").size();

        long pendingSelf = selfReviewRepository.countByOrganizationIdAndStatus(orgId, "Pending") + 
                           selfReviewRepository.countByOrganizationIdAndStatus(orgId, "Draft");
                           
        long completedSelf = selfReviewRepository.countByOrganizationIdAndStatus(orgId, "Submitted");

        long pendingManager = managerReviewRepository.countByOrganizationIdAndStatus(orgId, "Pending");
        long completedManager = managerReviewRepository.countByOrganizationIdAndStatus(orgId, "Completed") +
                                managerReviewRepository.countByOrganizationIdAndStatus(orgId, "Submitted");

        // Calculate real average rating from finalized manager reviews or calibration
        List<ManagerReview> completedReviews = managerReviewRepository.findByOrganizationIdAndStatus(orgId, "Completed");
        double averageRating = completedReviews.stream()
                .filter(r -> r.getOverallRating() != null)
                .mapToDouble(r -> r.getOverallRating().doubleValue())
                .average().orElse(0.0);

        // Top Goals
        List<PerformanceDashboardKpiDTO.TopGoalDTO> topGoals = goalRepository.findTop3ByOrganizationIdOrderByProgressDesc(orgId).stream()
                .map(g -> PerformanceDashboardKpiDTO.TopGoalDTO.builder()
                        .id(g.getId().toString())
                        .title(g.getTitle())
                        .progress(g.getProgress())
                        .employeeName(g.getEmployee() != null ? g.getEmployee().getFirstName() + " " + g.getEmployee().getLastName() : "")
                        .build())
                .collect(Collectors.toList());

        // Cycle Statuses
        List<PerformanceDashboardKpiDTO.CycleStatusDTO> cycleStatuses = cycleRepository.findByOrganizationId(orgId).stream()
                .map(c -> PerformanceDashboardKpiDTO.CycleStatusDTO.builder()
                        .name(c.getName())
                        .value(c.getCompletionPercentage() != null ? c.getCompletionPercentage() : 0)
                        .build())
                .collect(Collectors.toList());

        return PerformanceDashboardKpiDTO.builder()
                .activeCycles(activeCycles)
                .pendingSelf(pendingSelf)
                .pendingManager(pendingManager)
                .completedReviews(completedSelf + completedManager)
                .averageRating(averageRating)
                .topGoals(topGoals)
                .cycleStatuses(cycleStatuses)
                .departmentRatings(getDepartmentRatings(null))
                .build();
    }

    @Transactional(readOnly = true)
    public List<DepartmentRatingDTO> getDepartmentRatings(UUID cycleId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        List<PerformanceGoal> allGoals;
        
        if (cycleId != null) {
            allGoals = goalRepository.findByOrganizationIdAndCycleId(orgId, cycleId);
        } else {
            allGoals = goalRepository.findByOrganizationId(orgId);
        }
        
        Map<String, DepartmentData> deptMap = new HashMap<>();
        
        for (PerformanceGoal g : allGoals) {
            String deptName = (g.getEmployee() != null && g.getEmployee().getDepartment() != null) 
                    ? g.getEmployee().getDepartment().getDepartmentName() : "Unknown";
            
            deptMap.putIfAbsent(deptName, new DepartmentData(deptName));
            DepartmentData data = deptMap.get(deptName);
            
            if (g.getEmployee() != null) {
                data.employees.add(g.getEmployee().getId());
            }
            
            double rating = g.getProgress() != null ? g.getProgress() / 20.0 : 0.0;
            if (rating > 0) {
                data.totalRating += rating;
                data.ratedGoalsCount++;
            }
            
            if ("Completed".equals(g.getStatus()) || (g.getProgress() != null && g.getProgress() >= 100)) {
                data.topPerformers++;
            } else if (g.getProgress() != null && g.getProgress() < 50 && !"Draft".equals(g.getStatus())) {
                data.needsImprovement++;
            }
        }
        
        List<DepartmentRatingDTO> results = new ArrayList<>();
        for (DepartmentData data : deptMap.values()) {
            double avgRating = data.ratedGoalsCount > 0 ? data.totalRating / data.ratedGoalsCount : 0.0;
            
            results.add(DepartmentRatingDTO.builder()
                    .department(data.name)
                    .name(data.name)
                    .totalEmployees(data.employees.size())
                    .avgRating(avgRating)
                    .rating(avgRating)
                    .topPerformers(data.topPerformers)
                    .needsImprovement(data.needsImprovement)
                    .build());
        }
        
        return results;
    }

    private static class DepartmentData {
        String name;
        double totalRating = 0.0;
        int ratedGoalsCount = 0;
        int topPerformers = 0;
        int needsImprovement = 0;
        Set<UUID> employees = new HashSet<>();

        DepartmentData(String name) {
            this.name = name;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPromotionRecommendations(UUID cycleId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        List<ManagerReview> reviews;
        
        if (cycleId != null) {
            reviews = managerReviewRepository.findByOrganizationIdAndCycleId(orgId, cycleId);
        } else {
            reviews = managerReviewRepository.findByOrganizationId(orgId);
        }
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (ManagerReview r : reviews) {
            if (r.getPromotionRecommendation() != null && !r.getPromotionRecommendation().isBlank()) {
                Map<String, Object> map = new HashMap<>();
                map.put("employeeName", r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName());
                map.put("department", r.getEmployee().getDepartment() != null ? r.getEmployee().getDepartment().getDepartmentName() : "Unknown");
                map.put("recommendation", r.getPromotionRecommendation());
                map.put("managerName", r.getManager().getFirstName() + " " + r.getManager().getLastName());
                recommendations.add(map);
            }
        }
        return recommendations;
    }
}
