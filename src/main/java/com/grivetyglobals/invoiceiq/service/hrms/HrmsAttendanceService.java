package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ShiftRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ShiftRotationPatternRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ShiftRosterRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.HolidayListRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.HolidayRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceSchemeRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.IpMappingRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LockConfigurationRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveTypeRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveSchemeRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveSchemeRuleRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveBalanceRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveApplicationRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceRecordRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.RegularizationRequestRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.PermissionRequestRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceExceptionRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceLogRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceDeviceRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendancePeriodRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ProcessedAttendanceRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ApprovalHistoryRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.ReviewerAssignmentRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrmsAttendanceService {

    private final ShiftRepository shiftRepository;
    private final ShiftRotationPatternRepository shiftRotationPatternRepository;
    private final ShiftRosterRepository shiftRosterRepository;
    private final HolidayListRepository holidayListRepository;
    private final HolidayRepository holidayRepository;
    private final AttendanceSchemeRepository attendanceSchemeRepository;
    private final IpMappingRepository ipMappingRepository;
    private final LockConfigurationRepository lockConfigurationRepository;

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveSchemeRepository leaveSchemeRepository;
    private final LeaveSchemeRuleRepository leaveSchemeRuleRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final RegularizationRequestRepository regularizationRequestRepository;
    private final PermissionRequestRepository permissionRequestRepository;
    private final AttendanceExceptionRepository attendanceExceptionRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AttendanceDeviceRepository attendanceDeviceRepository;

    private final AttendancePeriodRepository attendancePeriodRepository;
    private final ProcessedAttendanceRepository processedAttendanceRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;

    private Organization getCurrentOrganization() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    private void recordApprovalHistory(UUID refId, String module, String action, String remarks) {
        try {
            ApprovalHistory history = ApprovalHistory.builder()
                    .organization(getCurrentOrganization())
                    .referenceId(refId)
                    .module(module)
                    .action(action)
                    .performedBy(SecurityUtils.getCurrentUsername())
                    .performedOn(LocalDateTime.now())
                    .remarks(remarks)
                    .build();
            approvalHistoryRepository.save(history);
        } catch (Exception e) {
            log.warn("Failed to record approval history for module={} action={} refId={}: {}", module, action, refId, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // SHIFTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Shift> getAllShifts() {
        return shiftRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public Shift getShiftById(UUID id) {
        var shift = shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
        if (!shift.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return shift;
    }

    @Transactional
    public Shift createShift(Shift shift) {
        shift.setOrganization(getCurrentOrganization());
        if (shift.getActive() == null) shift.setActive(true);
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift updateShift(UUID id, Shift updated) {
        Shift shift = getShiftById(id);
        shift.setShiftName(updated.getShiftName());
        shift.setShiftCode(updated.getShiftCode());
        shift.setDescription(updated.getDescription());
        shift.setStartTime(updated.getStartTime());
        shift.setEndTime(updated.getEndTime());
        shift.setGraceTime(updated.getGraceTime());
        shift.setLateGraceMinutes(updated.getLateGraceMinutes());
        shift.setEarlyExitGraceMinutes(updated.getEarlyExitGraceMinutes());
        shift.setHalfDayHours(updated.getHalfDayHours());
        shift.setFullDayHours(updated.getFullDayHours());
        shift.setBreakStart(updated.getBreakStart());
        shift.setBreakEnd(updated.getBreakEnd());
        shift.setFlexibleShift(updated.getFlexibleShift());
        shift.setOvertimeAllowed(updated.getOvertimeAllowed());
        shift.setNightShift(updated.getNightShift());
        if (updated.getActive() != null) shift.setActive(updated.getActive());
        return shiftRepository.save(shift);
    }

    @Transactional
    public void deleteShift(UUID id) {
        getShiftById(id); // validates ownership
        shiftRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // SHIFT ROTATION PATTERNS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShiftRotationPattern> getShiftRotationPatterns() {
        return shiftRotationPatternRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ShiftRotationPattern getShiftRotationPatternById(UUID id) {
        var shiftRotationPattern = shiftRotationPatternRepository.findById(id).orElseThrow(() -> new RuntimeException("Rotation pattern not found"));
        if (!shiftRotationPattern.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return shiftRotationPattern;
    }

    @Transactional
    public ShiftRotationPattern createShiftRotationPattern(ShiftRotationPattern pattern) {
        pattern.setOrganization(getCurrentOrganization());
        return shiftRotationPatternRepository.save(pattern);
    }

    @Transactional
    public ShiftRotationPattern updateShiftRotationPattern(UUID id, ShiftRotationPattern updated) {
        ShiftRotationPattern pattern = getShiftRotationPatternById(id);
        pattern.setPatternName(updated.getPatternName());
        pattern.setRotationDays(updated.getRotationDays());
        pattern.setPatternSequence(updated.getPatternSequence());
        return shiftRotationPatternRepository.save(pattern);
    }

    @Transactional
    public void deleteShiftRotationPattern(UUID id) {
        getShiftRotationPatternById(id); // validates ownership
        shiftRotationPatternRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // SHIFT ROSTERS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShiftRoster> getShiftRosters() {
        return shiftRosterRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ShiftRoster getShiftRosterById(UUID id) {
        var shiftRoster = shiftRosterRepository.findById(id).orElseThrow(() -> new RuntimeException("Roster not found"));
        if (!shiftRoster.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return shiftRoster;
    }

    @Transactional
    public ShiftRoster createShiftRoster(ShiftRoster roster) {
        roster.setOrganization(getCurrentOrganization());
        if (roster.getPublished() == null) roster.setPublished(false);
        if (roster.getOverridden() == null) roster.setOverridden(false);
        return shiftRosterRepository.save(roster);
    }

    @Transactional
    public ShiftRoster updateShiftRoster(UUID id, ShiftRoster updated) {
        ShiftRoster roster = getShiftRosterById(id);
        if (updated.getAssignedShift() != null) roster.setAssignedShift(updated.getAssignedShift());
        if (updated.getRosterDate() != null) roster.setRosterDate(updated.getRosterDate());
        if (updated.getOverridden() != null) roster.setOverridden(updated.getOverridden());
        if (updated.getPublished() != null) roster.setPublished(updated.getPublished());
        return shiftRosterRepository.save(roster);
    }

    @Transactional
    public void deleteShiftRoster(UUID id) {
        getShiftRosterById(id); // validates ownership
        shiftRosterRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // HOLIDAY LIST & HOLIDAYS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<HolidayList> getHolidayLists() {
        return holidayListRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public HolidayList getHolidayListById(UUID id) {
        var holidayList = holidayListRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday list not found"));
        if (!holidayList.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return holidayList;
    }

    @Transactional
    public HolidayList createHolidayList(HolidayList list) {
        list.setOrganization(getCurrentOrganization());
        return holidayListRepository.save(list);
    }

    @Transactional
    public HolidayList updateHolidayList(UUID id, HolidayList updated) {
        HolidayList list = getHolidayListById(id);
        list.setHolidayList(updated.getHolidayList());
        list.setYear(updated.getYear());
        list.setLocationId(updated.getLocationId());
        return holidayListRepository.save(list);
    }

    @Transactional
    public void deleteHolidayList(UUID id) {
        getHolidayListById(id); // validates ownership
        holidayListRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysByListId(UUID listId) {
        return holidayRepository.findByHolidayListId(listId);
    }

    @Transactional(readOnly = true)
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findByHolidayListOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public Holiday getHolidayById(UUID id) {
        var holiday = holidayRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday not found"));
        if (holiday.getHolidayList() != null && !holiday.getHolidayList().getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return holiday;
    }

    @Transactional
    public Holiday createHoliday(UUID listId, Holiday holiday) {
        HolidayList list = getHolidayListById(listId);
        holiday.setHolidayList(list);
        if (holiday.getActive() == null) holiday.setActive(true);
        return holidayRepository.save(holiday);
    }

    @Transactional
    public Holiday createHolidayDirect(Holiday holiday) {
        if (holiday.getHolidayList() != null && holiday.getHolidayList().getId() != null) {
            HolidayList list = getHolidayListById(holiday.getHolidayList().getId());
            holiday.setHolidayList(list);
        }
        if (holiday.getActive() == null) holiday.setActive(true);
        return holidayRepository.save(holiday);
    }


    @Transactional
    public Holiday updateHoliday(UUID id, Holiday updated) {
        Holiday holiday = getHolidayById(id);
        holiday.setHolidayDate(updated.getHolidayDate());
        holiday.setHolidayName(updated.getHolidayName());
        holiday.setHolidayType(updated.getHolidayType());
        holiday.setLocation(updated.getLocation());
        holiday.setOptional(updated.getOptional());
        holiday.setRestrictedHoliday(updated.getRestrictedHoliday());
        holiday.setDescription(updated.getDescription());
        if (updated.getActive() != null) holiday.setActive(updated.getActive());
        return holidayRepository.save(holiday);
    }

    @Transactional
    public void deleteHoliday(UUID id) {
        getHolidayById(id); // validates ownership via parent HolidayList
        holidayRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // ATTENDANCE SCHEMES, IP MAPPINGS, LOCK CONFIG
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceScheme> getAttendanceSchemes() {
        return attendanceSchemeRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public AttendanceScheme getAttendanceSchemeById(UUID id) {
        var attendanceScheme = attendanceSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scheme not found"));
        if (!attendanceScheme.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendanceScheme;
    }

    @Transactional
    public AttendanceScheme createAttendanceScheme(AttendanceScheme scheme) {
        scheme.setOrganization(getCurrentOrganization());
        return attendanceSchemeRepository.save(scheme);
    }

    @Transactional
    public AttendanceScheme updateAttendanceScheme(UUID id, AttendanceScheme updated) {
        AttendanceScheme scheme = getAttendanceSchemeById(id);
        scheme.setSchemeName(updated.getSchemeName());
        scheme.setSchemeDescription(updated.getSchemeDescription());
        scheme.setDefaultShift(updated.getDefaultShift());
        scheme.setHolidayList(updated.getHolidayList());
        scheme.setWeekendConfiguration(updated.getWeekendConfiguration());
        scheme.setRequireLiveValidation(updated.getRequireLiveValidation());
        scheme.setLatePolicy(updated.getLatePolicy());
        scheme.setOvertimePolicy(updated.getOvertimePolicy());
        scheme.setMinimumHours(updated.getMinimumHours());
        scheme.setHalfDayHours(updated.getHalfDayHours());
        scheme.setGraceMinutes(updated.getGraceMinutes());
        scheme.setAutoRegularization(updated.getAutoRegularization());
        scheme.setAllowMobileAttendance(updated.getAllowMobileAttendance());
        return attendanceSchemeRepository.save(scheme);
    }

    @Transactional
    public void deleteAttendanceScheme(UUID id) {
        getAttendanceSchemeById(id); // validates ownership
        attendanceSchemeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<IpMapping> getIpMappings() {
        return ipMappingRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public IpMapping getIpMappingById(UUID id) {
        var ipMapping = ipMappingRepository.findById(id).orElseThrow(() -> new RuntimeException("IP Mapping not found"));
        if (!ipMapping.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return ipMapping;
    }

    @Transactional
    public IpMapping createIpMapping(IpMapping mapping) {
        mapping.setOrganization(getCurrentOrganization());
        return ipMappingRepository.save(mapping);
    }

    @Transactional
    public IpMapping updateIpMapping(UUID id, IpMapping updated) {
        IpMapping mapping = getIpMappingById(id);
        mapping.setLocationId(updated.getLocationId());
        mapping.setIpAddress(updated.getIpAddress());
        mapping.setDescription(updated.getDescription());
        return ipMappingRepository.save(mapping);
    }

    @Transactional
    public void deleteIpMapping(UUID id) {
        getIpMappingById(id); // validates ownership
        ipMappingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LockConfiguration> getLockConfigurations() {
        return lockConfigurationRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public LockConfiguration getLockConfigurationById(UUID id) {
        var lockConfiguration = lockConfigurationRepository.findById(id).orElseThrow(() -> new RuntimeException("Lock config not found"));
        if (!lockConfiguration.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return lockConfiguration;
    }

    @Transactional
    public LockConfiguration createLockConfiguration(LockConfiguration config) {
        config.setOrganization(getCurrentOrganization());
        return lockConfigurationRepository.save(config);
    }

    @Transactional
    public LockConfiguration updateLockConfiguration(UUID id, LockConfiguration updated) {
        LockConfiguration config = getLockConfigurationById(id);
        config.setFeature(updated.getFeature());
        config.setLockDays(updated.getLockDays());
        config.setActive(updated.getActive());
        return lockConfigurationRepository.save(config);
    }

    @Transactional
    public void deleteLockConfiguration(UUID id) {
        getLockConfigurationById(id); // validates ownership
        lockConfigurationRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // LEAVE TYPES & SCHEMES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LeaveType> getLeaveTypes() {
        return leaveTypeRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeById(UUID id) {
        var leaveType = leaveTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave type not found"));
        if (!leaveType.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return leaveType;
    }

    @Transactional
    public LeaveType createLeaveType(LeaveType type) {
        type.setOrganization(getCurrentOrganization());
        if (type.getActive() == null) type.setActive(true);
        return leaveTypeRepository.save(type);
    }

    @Transactional
    public LeaveType updateLeaveType(UUID id, LeaveType updated) {
        LeaveType type = getLeaveTypeById(id);
        type.setName(updated.getName());
        type.setCode(updated.getCode());
        type.setCategory(updated.getCategory());
        type.setDescription(updated.getDescription());
        if (updated.getActive() != null) type.setActive(updated.getActive());
        type.setColor(updated.getColor());
        type.setIcon(updated.getIcon());
        type.setRequiresApproval(updated.getRequiresApproval());
        type.setRequiresAttachment(updated.getRequiresAttachment());
        type.setMinimumDays(updated.getMinimumDays());
        type.setMaximumDays(updated.getMaximumDays());
        type.setGenderRestriction(updated.getGenderRestriction());
        type.setProbationAllowed(updated.getProbationAllowed());
        type.setNoticePeriodRequired(updated.getNoticePeriodRequired());
        type.setAllowHalfDay(updated.getAllowHalfDay());
        type.setAllowHourlyLeave(updated.getAllowHourlyLeave());
        return leaveTypeRepository.save(type);
    }

    @Transactional
    public void deleteLeaveType(UUID id) {
        getLeaveTypeById(id); // validates ownership
        leaveTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LeaveScheme> getLeaveSchemes() {
        return leaveSchemeRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public LeaveScheme getLeaveSchemeById(UUID id) {
        var leaveScheme = leaveSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave scheme not found"));
        if (!leaveScheme.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return leaveScheme;
    }

    @Transactional
    public LeaveScheme createLeaveScheme(LeaveScheme scheme) {
        scheme.setOrganization(getCurrentOrganization());
        return leaveSchemeRepository.save(scheme);
    }

    @Transactional
    public LeaveScheme updateLeaveScheme(UUID id, LeaveScheme updated) {
        LeaveScheme scheme = getLeaveSchemeById(id);
        scheme.setSchemeName(updated.getSchemeName());
        scheme.setDefaultScheme(updated.getDefaultScheme());
        scheme.setDescription(updated.getDescription());
        return leaveSchemeRepository.save(scheme);
    }

    @Transactional
    public void deleteLeaveScheme(UUID id) {
        getLeaveSchemeById(id); // validates ownership
        leaveSchemeRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // LEAVE BALANCES & APPLICATIONS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LeaveBalance> getLeaveBalances(UUID employeeId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        if (employeeId != null) {
            // Restrict to this org's employees only
            return leaveBalanceRepository.findByEmployeeOrganizationIdAndEmployeeId(orgId, employeeId);
        }
        return leaveBalanceRepository.findByEmployeeOrganizationId(orgId);
    }

    @Transactional(readOnly = true)
    public List<LeaveApplication> getLeaveApplicationsFiltered(String status, UUID employeeId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        if (status != null && !status.isEmpty() && employeeId != null) {
            return leaveApplicationRepository.findByOrganizationIdAndStatus(orgId, status)
                    .stream().filter(l -> l.getEmployee() != null && employeeId.equals(l.getEmployee().getId()))
                    .collect(Collectors.toList());
        } else if (status != null && !status.isEmpty()) {
            return leaveApplicationRepository.findByOrganizationIdAndStatus(orgId, status);
        } else if (employeeId != null) {
            return leaveApplicationRepository.findByOrganizationIdAndEmployeeId(orgId, employeeId);
        }
        return leaveApplicationRepository.findByOrganizationId(orgId);
    }

    @Transactional(readOnly = true)
    public LeaveApplication getLeaveApplicationById(UUID id) {
        var leaveApplication = leaveApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        if (!leaveApplication.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return leaveApplication;
    }

    @Transactional
    public LeaveApplication createLeaveApplication(LeaveApplication app) {
        app.setOrganization(getCurrentOrganization());
        if (app.getStatus() == null) app.setStatus("Pending");
        if (app.getAppliedOn() == null) app.setAppliedOn(LocalDate.now());
        if (app.getLeaveNumber() == null) {
            app.setLeaveNumber("LV-" + System.currentTimeMillis() % 100000);
        }
        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public LeaveApplication updateLeaveApplication(UUID id, LeaveApplication updated) {
        LeaveApplication app = getLeaveApplicationById(id);
        if (updated.getReason() != null) app.setReason(updated.getReason());
        if (updated.getFromDate() != null) app.setFromDate(updated.getFromDate());
        if (updated.getToDate() != null) app.setToDate(updated.getToDate());
        if (updated.getDuration() != null) app.setDuration(updated.getDuration());
        if (updated.getStatus() != null) app.setStatus(updated.getStatus());
        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public void deleteLeaveApplication(UUID id) {
        getLeaveApplicationById(id); // validates ownership
        leaveApplicationRepository.deleteById(id);
    }

    @Transactional
    public LeaveApplication approveLeaveApplication(UUID id, String remarks) {
        LeaveApplication app = getLeaveApplicationById(id);
        app.setStatus("Approved");
        app.setApprovalRemarks(remarks);
        app.setApprovedAt(LocalDateTime.now());

        // Deduct from leave balance
        int currentYear = app.getFromDate() != null ? app.getFromDate().getYear() : LocalDate.now().getYear();
        if (app.getEmployee() != null && app.getLeaveType() != null) {
            leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(app.getEmployee().getId(), app.getLeaveType().getId(), currentYear)
                    .ifPresent(bal -> {
                        BigDecimal availed = bal.getAvailed() != null ? bal.getAvailed() : BigDecimal.ZERO;
                        BigDecimal duration = app.getDuration() != null ? app.getDuration() : BigDecimal.ONE;
                        bal.setAvailed(availed.add(duration));
                        BigDecimal avail = bal.getAvailable() != null ? bal.getAvailable() : BigDecimal.ZERO;
                        bal.setAvailable(avail.subtract(duration));
                        leaveBalanceRepository.save(bal);
                    });
        }

        recordApprovalHistory(app.getId(), "Leave", "Approved", remarks);
        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public LeaveApplication rejectLeaveApplication(UUID id, String remarks) {
        LeaveApplication app = getLeaveApplicationById(id);
        app.setStatus("Rejected");
        app.setApprovalRemarks(remarks);
        app.setApprovedAt(LocalDateTime.now());
        recordApprovalHistory(app.getId(), "Leave", "Rejected", remarks);
        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public LeaveApplication cancelLeaveApplication(UUID id, String reason) {
        LeaveApplication app = getLeaveApplicationById(id);
        app.setStatus("Cancelled");
        app.setCancelReason(reason);
        recordApprovalHistory(app.getId(), "Leave", "Cancelled", reason);
        return leaveApplicationRepository.save(app);
    }

    // ─────────────────────────────────────────────────────────
    // ATTENDANCE RECORDS, CHECK-IN/OUT
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecordRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceRecordsFiltered(String status, UUID employeeId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        if (status != null && !status.isEmpty() && employeeId != null) {
            return attendanceRecordRepository.findByOrganizationIdAndStatus(orgId, status)
                    .stream().filter(r -> r.getEmployee() != null && employeeId.equals(r.getEmployee().getId()))
                    .collect(Collectors.toList());
        } else if (status != null && !status.isEmpty()) {
            return attendanceRecordRepository.findByOrganizationIdAndStatus(orgId, status);
        } else if (employeeId != null) {
            return attendanceRecordRepository.findByOrganizationIdAndEmployeeId(orgId, employeeId);
        }
        return getAttendanceRecords();
    }

    @Transactional(readOnly = true)
    public AttendanceRecord getAttendanceRecordById(UUID id) {
        var attendanceRecord = attendanceRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Attendance record not found"));
        if (!attendanceRecord.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendanceRecord;
    }

    @Transactional
    public AttendanceRecord createOrUpdateRecord(AttendanceRecord rec) {
        rec.setOrganization(getCurrentOrganization());
        return attendanceRecordRepository.save(rec);
    }

    @Transactional
    public AttendanceRecord updateAttendanceRecord(UUID id, AttendanceRecord updated) {
        AttendanceRecord rec = getAttendanceRecordById(id);
        if (updated.getCheckIn() != null) rec.setCheckIn(updated.getCheckIn());
        if (updated.getCheckOut() != null) rec.setCheckOut(updated.getCheckOut());
        if (updated.getStatus() != null) rec.setStatus(updated.getStatus());
        if (updated.getWorkingHours() != null) rec.setWorkingHours(updated.getWorkingHours());
        if (updated.getRemarks() != null) rec.setRemarks(updated.getRemarks());
        return attendanceRecordRepository.save(rec);
    }

    @Transactional
    public void deleteAttendanceRecord(UUID id) {
        getAttendanceRecordById(id); // validates ownership
        attendanceRecordRepository.deleteById(id);
    }

    @Transactional
    public AttendanceRecord checkIn(UUID employeeId, String source) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!employee.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        LocalDate today = LocalDate.now();

        AttendanceRecord record = attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElse(AttendanceRecord.builder()
                        .organization(getCurrentOrganization())
                        .employee(employee)
                        .attendanceDate(today)
                        .status("Present")
                        .attendanceSource(source != null ? source : "Web")
                        .build());

        record.setCheckIn(LocalDateTime.now());
        record.setStatus("Present");
        return attendanceRecordRepository.save(record);
    }

    @Transactional
    public AttendanceRecord checkOut(UUID employeeId) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("Check-in record not found for today"));

        record.setCheckOut(LocalDateTime.now());
        if (record.getCheckIn() != null) {
            long minutes = Duration.between(record.getCheckIn(), record.getCheckOut()).toMinutes();
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            record.setWorkingHours(hours);
        }
        return attendanceRecordRepository.save(record);
    }

    // ─────────────────────────────────────────────────────────
    // REGULARIZATION & PERMISSION REQUESTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RegularizationRequest> getRegularizationRequests() {
        return regularizationRequestRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<RegularizationRequest> getRegularizationRequestsFiltered(String status) {
        if (status != null && !status.isEmpty()) {
            return regularizationRequestRepository.findByOrganizationIdAndStatus(SecurityUtils.getCurrentOrganizationId(), status);
        }
        return getRegularizationRequests();
    }

    @Transactional(readOnly = true)
    public RegularizationRequest getRegularizationRequestById(UUID id) {
        var regularizationRequest = regularizationRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        if (!regularizationRequest.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return regularizationRequest;
    }

    @Transactional
    public RegularizationRequest createRegularizationRequest(RegularizationRequest req) {
        req.setOrganization(getCurrentOrganization());
        if (req.getStatus() == null) req.setStatus("Pending");
        if (req.getRequestNumber() == null) {
            req.setRequestNumber("REG-" + System.currentTimeMillis() % 100000);
        }
        return regularizationRequestRepository.save(req);
    }

    @Transactional
    public RegularizationRequest updateRegularizationRequest(UUID id, RegularizationRequest updated) {
        RegularizationRequest req = getRegularizationRequestById(id);
        if (updated.getReason() != null) req.setReason(updated.getReason());
        if (updated.getRequestedCheckIn() != null) req.setRequestedCheckIn(updated.getRequestedCheckIn());
        if (updated.getRequestedCheckOut() != null) req.setRequestedCheckOut(updated.getRequestedCheckOut());
        if (updated.getStatus() != null) req.setStatus(updated.getStatus());
        return regularizationRequestRepository.save(req);
    }

    @Transactional
    public void deleteRegularizationRequest(UUID id) {
        getRegularizationRequestById(id); // validates ownership
        regularizationRequestRepository.deleteById(id);
    }

    @Transactional
    public RegularizationRequest approveRegularizationRequest(UUID id, String remarks) {
        RegularizationRequest req = getRegularizationRequestById(id);
        req.setStatus("Approved");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());

        if (req.getEmployee() != null && req.getDate() != null) {
            attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(req.getEmployee().getId(), req.getDate())
                    .ifPresent(rec -> {
                        rec.setRegularized(true);
                        rec.setRegularizationStatus("Approved");
                        attendanceRecordRepository.save(rec);
                    });
        }

        recordApprovalHistory(req.getId(), "Regularization", "Approved", remarks);
        return regularizationRequestRepository.save(req);
    }

    @Transactional
    public RegularizationRequest rejectRegularizationRequest(UUID id, String remarks) {
        RegularizationRequest req = getRegularizationRequestById(id);
        req.setStatus("Rejected");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        recordApprovalHistory(req.getId(), "Regularization", "Rejected", remarks);
        return regularizationRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public List<PermissionRequest> getPermissionRequests() {
        return permissionRequestRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<PermissionRequest> getPermissionRequestsFiltered(String status) {
        if (status != null && !status.isEmpty()) {
            return permissionRequestRepository.findByOrganizationIdAndStatus(SecurityUtils.getCurrentOrganizationId(), status);
        }
        return getPermissionRequests();
    }

    @Transactional(readOnly = true)
    public PermissionRequest getPermissionRequestById(UUID id) {
        var permissionRequest = permissionRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        if (!permissionRequest.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return permissionRequest;
    }

    @Transactional
    public PermissionRequest createPermissionRequest(PermissionRequest req) {
        req.setOrganization(getCurrentOrganization());
        if (req.getStatus() == null) req.setStatus("Pending");
        if (req.getPermissionNumber() == null) {
            req.setPermissionNumber("PERM-" + System.currentTimeMillis() % 100000);
        }
        return permissionRequestRepository.save(req);
    }

    @Transactional
    public PermissionRequest updatePermissionRequest(UUID id, PermissionRequest updated) {
        PermissionRequest req = getPermissionRequestById(id);
        if (updated.getReason() != null) req.setReason(updated.getReason());
        if (updated.getStartTime() != null) req.setStartTime(updated.getStartTime());
        if (updated.getEndTime() != null) req.setEndTime(updated.getEndTime());
        if (updated.getStatus() != null) req.setStatus(updated.getStatus());
        return permissionRequestRepository.save(req);
    }

    @Transactional
    public void deletePermissionRequest(UUID id) {
        getPermissionRequestById(id); // validates ownership
        permissionRequestRepository.deleteById(id);
    }

    @Transactional
    public PermissionRequest approvePermissionRequest(UUID id, String remarks) {
        PermissionRequest req = getPermissionRequestById(id);
        req.setStatus("Approved");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        recordApprovalHistory(req.getId(), "Permission", "Approved", remarks);
        return permissionRequestRepository.save(req);
    }

    @Transactional
    public PermissionRequest rejectPermissionRequest(UUID id, String remarks) {
        PermissionRequest req = getPermissionRequestById(id);
        req.setStatus("Rejected");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        recordApprovalHistory(req.getId(), "Permission", "Rejected", remarks);
        return permissionRequestRepository.save(req);
    }

    // ─────────────────────────────────────────────────────────
    // EXCEPTIONS, LOGS, DEVICES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceException> getAttendanceExceptions() {
        return attendanceExceptionRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<AttendanceException> getAttendanceExceptionsFiltered(Boolean resolved) {
        if (resolved != null) {
            return attendanceExceptionRepository.findByOrganizationIdAndResolved(SecurityUtils.getCurrentOrganizationId(), resolved);
        }
        return getAttendanceExceptions();
    }

    @Transactional(readOnly = true)
    public AttendanceException getAttendanceExceptionById(UUID id) {
        var attendanceException = attendanceExceptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception not found"));
        if (!attendanceException.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendanceException;
    }

    @Transactional
    public AttendanceException createAttendanceException(AttendanceException exc) {
        exc.setOrganization(getCurrentOrganization());
        if (exc.getResolved() == null) exc.setResolved(false);
        return attendanceExceptionRepository.save(exc);
    }

    @Transactional
    public AttendanceException updateAttendanceException(UUID id, AttendanceException updated) {
        AttendanceException exc = getAttendanceExceptionById(id);
        if (updated.getDescription() != null) exc.setDescription(updated.getDescription());
        if (updated.getSeverity() != null) exc.setSeverity(updated.getSeverity());
        if (updated.getResolved() != null) exc.setResolved(updated.getResolved());
        return attendanceExceptionRepository.save(exc);
    }

    @Transactional
    public void deleteAttendanceException(UUID id) {
        getAttendanceExceptionById(id); // validates ownership
        attendanceExceptionRepository.deleteById(id);
    }

    @Transactional
    public AttendanceException resolveAttendanceException(UUID id) {
        AttendanceException exc = getAttendanceExceptionById(id);
        exc.setResolved(true);
        exc.setResolvedAt(LocalDateTime.now());
        return attendanceExceptionRepository.save(exc);
    }

    @Transactional(readOnly = true)
    public List<AttendanceLog> getAttendanceLogs() {
        return attendanceLogRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public AttendanceLog getAttendanceLogById(UUID id) {
        var attendanceLog = attendanceLogRepository.findById(id).orElseThrow(() -> new RuntimeException("Log not found"));
        if (!attendanceLog.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendanceLog;
    }

    @Transactional
    public AttendanceLog ingestAttendanceLog(AttendanceLog log) {
        log.setOrganization(getCurrentOrganization());
        if (log.getTimestamp() == null) log.setTimestamp(LocalDateTime.now());
        return attendanceLogRepository.save(log);
    }

    @Transactional
    public AttendanceLog updateAttendanceLog(UUID id, AttendanceLog updated) {
        AttendanceLog log = getAttendanceLogById(id);
        if (updated.getDirection() != null) log.setDirection(updated.getDirection());
        if (updated.getDevice() != null) log.setDevice(updated.getDevice());
        return attendanceLogRepository.save(log);
    }

    @Transactional
    public void deleteAttendanceLog(UUID id) {
        getAttendanceLogById(id); // validates ownership
        attendanceLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDevice> getAttendanceDevices() {
        return attendanceDeviceRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public AttendanceDevice getAttendanceDeviceById(UUID id) {
        var attendanceDevice = attendanceDeviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
        if (!attendanceDevice.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendanceDevice;
    }

    @Transactional
    public AttendanceDevice createAttendanceDevice(AttendanceDevice device) {
        device.setOrganization(getCurrentOrganization());
        if (device.getStatus() == null) device.setStatus("Active");
        return attendanceDeviceRepository.save(device);
    }

    @Transactional
    public AttendanceDevice updateAttendanceDevice(UUID id, AttendanceDevice updated) {
        AttendanceDevice device = getAttendanceDeviceById(id);
        device.setDeviceName(updated.getDeviceName());
        device.setDeviceType(updated.getDeviceType());
        device.setLocation(updated.getLocation());
        device.setIpAddress(updated.getIpAddress());
        if (updated.getStatus() != null) device.setStatus(updated.getStatus());
        return attendanceDeviceRepository.save(device);
    }

    @Transactional
    public void deleteAttendanceDevice(UUID id) {
        getAttendanceDeviceById(id); // validates ownership
        attendanceDeviceRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // PROCESSING & PERIODS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendancePeriod> getAttendancePeriods() {
        return attendancePeriodRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public AttendancePeriod getAttendancePeriodById(UUID id) {
        var attendancePeriod = attendancePeriodRepository.findById(id).orElseThrow(() -> new RuntimeException("Period not found"));
        if (!attendancePeriod.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return attendancePeriod;
    }

    @Transactional
    public AttendancePeriod createAttendancePeriod(AttendancePeriod period) {
        period.setOrganization(getCurrentOrganization());
        if (period.getStatus() == null) period.setStatus("Open");
        if (period.getProcessingStatus() == null) period.setProcessingStatus("Open");
        return attendancePeriodRepository.save(period);
    }

    @Transactional
    public AttendancePeriod updateAttendancePeriod(UUID id, AttendancePeriod updated) {
        AttendancePeriod period = getAttendancePeriodById(id);
        if (updated.getPeriodName() != null) period.setPeriodName(updated.getPeriodName());
        if (updated.getStatus() != null) period.setStatus(updated.getStatus());
        if (updated.getProcessingStatus() != null) period.setProcessingStatus(updated.getProcessingStatus());
        return attendancePeriodRepository.save(period);
    }

    @Transactional
    public void deleteAttendancePeriod(UUID id) {
        getAttendancePeriodById(id); // validates ownership
        attendancePeriodRepository.deleteById(id);
    }

    @Transactional
    public AttendancePeriod lockAttendancePeriod(UUID id) {
        AttendancePeriod period = getAttendancePeriodById(id);
        period.setStatus("Locked");
        period.setProcessingStatus("Locked");
        period.setLockedAt(LocalDateTime.now());
        return attendancePeriodRepository.save(period);
    }

    @Transactional
    public AttendancePeriod processAttendancePeriod(UUID id) {
        AttendancePeriod period = getAttendancePeriodById(id);
        period.setProcessingStatus("Processed");
        period.setStatus("Processed");
        period.setProcessedOn(LocalDateTime.now());
        return attendancePeriodRepository.save(period);
    }

    @Transactional(readOnly = true)
    public List<ProcessedAttendance> getProcessedAttendanceByPeriodId(UUID periodId) {
        return processedAttendanceRepository.findByPeriodId(periodId);
    }

    @Transactional(readOnly = true)
    public ProcessedAttendance getProcessedAttendanceById(UUID id) {
        var processedAttendance = processedAttendanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Processed attendance not found"));
        if (processedAttendance.getPeriod() != null && !processedAttendance.getPeriod().getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return processedAttendance;
    }

    @Transactional
    public ProcessedAttendance createProcessedAttendance(ProcessedAttendance pa) {
        return processedAttendanceRepository.save(pa);
    }

    @Transactional
    public ProcessedAttendance updateProcessedAttendance(UUID id, ProcessedAttendance updated) {
        ProcessedAttendance pa = getProcessedAttendanceById(id);
        if (updated.getStatus() != null) pa.setStatus(updated.getStatus());
        if (updated.getPayableDays() != null) pa.setPayableDays(updated.getPayableDays());
        return processedAttendanceRepository.save(pa);
    }

    @Transactional
    public void deleteProcessedAttendance(UUID id) {
        getProcessedAttendanceById(id); // validates ownership via parent period
        processedAttendanceRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // APPROVAL HISTORIES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ApprovalHistory> getApprovalHistories() {
        return approvalHistoryRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public ApprovalHistory getApprovalHistoryById(UUID id) {
        var approvalHistory = approvalHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Approval history not found"));
        if (!approvalHistory.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return approvalHistory;
    }

    @Transactional
    public ApprovalHistory createApprovalHistory(ApprovalHistory history) {
        history.setOrganization(getCurrentOrganization());
        if (history.getPerformedOn() == null) history.setPerformedOn(LocalDateTime.now());
        return approvalHistoryRepository.save(history);
    }

    @Transactional
    public ApprovalHistory updateApprovalHistory(UUID id, ApprovalHistory updated) {
        ApprovalHistory h = getApprovalHistoryById(id);
        if (updated.getRemarks() != null) h.setRemarks(updated.getRemarks());
        if (updated.getAction() != null) h.setAction(updated.getAction());
        return approvalHistoryRepository.save(h);
    }

    @Transactional
    public void deleteApprovalHistory(UUID id) {
        getApprovalHistoryById(id); // validates ownership
        approvalHistoryRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // EMPLOYEE ATTENDANCE SUMMARIES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeAttendanceSummaries(UUID employeeId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        List<AttendanceRecord> records = employeeId != null ?
                attendanceRecordRepository.findByOrganizationIdAndEmployeeId(orgId, employeeId) :
                attendanceRecordRepository.findByOrganizationId(orgId);

        Map<UUID, List<AttendanceRecord>> grouped = records.stream()
                .filter(r -> r.getEmployee() != null)
                .collect(Collectors.groupingBy(r -> r.getEmployee().getId()));

        List<Map<String, Object>> summaries = new ArrayList<>();
        grouped.forEach((empId, empRecords) -> {
            long present = empRecords.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
            long absent = empRecords.stream().filter(r -> "Absent".equalsIgnoreCase(r.getStatus())).count();
            long leave = empRecords.stream().filter(r -> "Leave".equalsIgnoreCase(r.getStatus())).count();
            long late = empRecords.stream().filter(r -> r.getLateBy() != null && r.getLateBy() > 0).count();
            long halfDay = empRecords.stream().filter(r -> "Half Day".equalsIgnoreCase(r.getStatus())).count();
            double overtime = empRecords.stream()
                    .filter(r -> r.getOvertimeHours() != null)
                    .mapToDouble(r -> r.getOvertimeHours().doubleValue()).sum();

            Map<String, Object> summary = new HashMap<>();
            summary.put("id", empId.toString());
            summary.put("employeeId", empId.toString());
            summary.put("present", present);
            summary.put("absent", absent);
            summary.put("leave", leave);
            summary.put("late", late);
            summary.put("halfDay", halfDay);
            summary.put("overtime", overtime);
            summary.put("payableDays", present + leave + (halfDay * 0.5));
            summaries.add(summary);
        });

        return summaries;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeAttendanceSummaryById(UUID id) {
        List<Map<String, Object>> summaries = getEmployeeAttendanceSummaries(id);
        if (!summaries.isEmpty()) return summaries.get(0);

        Map<String, Object> empty = new HashMap<>();
        empty.put("id", id.toString());
        empty.put("employeeId", id.toString());
        empty.put("present", 0);
        empty.put("absent", 0);
        empty.put("leave", 0);
        empty.put("late", 0);
        empty.put("halfDay", 0);
        empty.put("overtime", 0);
        empty.put("payableDays", 0);
        return empty;
    }

    // ─────────────────────────────────────────────────────────
    // CHECK-IN / CHECK-OUT (Mobile)
    // ─────────────────────────────────────────────────────────

    @Transactional
    public AttendanceRecord checkIn(UUID employeeId, String source,
                                     BigDecimal latitude, BigDecimal longitude) {
        Organization org = getCurrentOrganization();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Guard: last punch must NOT be "In" (can't double-check-in)
        Optional<AttendanceLog> lastPunch = attendanceLogRepository
                .findTopByOrganizationIdAndEmployeeIdOrderByTimestampDesc(org.getId(), employeeId);
        if (lastPunch.isPresent()
                && "In".equals(lastPunch.get().getDirection())
                && lastPunch.get().getTimestamp().toLocalDate().isEqual(today)) {
            throw new RuntimeException("Already checked in. Please check out first.");
        }

        // Upsert the day record — set checkIn only on first punch of the day
        AttendanceRecord record = attendanceRecordRepository
                .findByOrganizationIdAndEmployeeIdAndAttendanceDate(org.getId(), employeeId, today)
                .orElseGet(() -> AttendanceRecord.builder()
                        .organization(org)
                        .employee(employee)
                        .attendanceDate(today)
                        .attendanceSource(source != null ? source : "Mobile")
                        .status("Present")
                        .build());
        if (record.getCheckIn() == null) {
            record.setCheckIn(now); // first check-in of the day
        }
        attendanceRecordRepository.save(record);

        // Write audit punch
        AttendanceLog punch = AttendanceLog.builder()
                .organization(org)
                .employee(employee)
                .timestamp(now)
                .direction("In")
                .source(source != null ? source : "Mobile")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        attendanceLogRepository.save(punch);

        return record;
    }

    @Transactional
    public AttendanceRecord checkOut(UUID employeeId,
                                      BigDecimal latitude, BigDecimal longitude) {
        Organization org = getCurrentOrganization();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Guard: last punch must be "In"
        Optional<AttendanceLog> lastPunch = attendanceLogRepository
                .findTopByOrganizationIdAndEmployeeIdOrderByTimestampDesc(org.getId(), employeeId);
        if (lastPunch.isEmpty()
                || !"In".equals(lastPunch.get().getDirection())
                || !lastPunch.get().getTimestamp().toLocalDate().isEqual(today)) {
            throw new RuntimeException("Not currently checked in.");
        }

        AttendanceRecord record = attendanceRecordRepository
                .findByOrganizationIdAndEmployeeIdAndAttendanceDate(org.getId(), employeeId, today)
                .orElseThrow(() -> new RuntimeException("No attendance record for today"));

        // Update last check-out time on the day record
        record.setCheckOut(now);
        // NOTE: workingHours is NOT computed here — deferred to the processing job
        attendanceRecordRepository.save(record);

        // Write audit punch
        AttendanceLog punch = AttendanceLog.builder()
                .organization(org)
                .employee(employee)
                .timestamp(now)
                .direction("Out")
                .source("Mobile")
                .latitude(latitude)
                .longitude(longitude)
                .build();
        attendanceLogRepository.save(punch);

        return record;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTodayCheckInStatus(UUID employeeId) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        LocalDate today = LocalDate.now();

        // Determine current state from last punch direction
        Optional<AttendanceLog> lastPunch = attendanceLogRepository
                .findTopByOrganizationIdAndEmployeeIdOrderByTimestampDesc(orgId, employeeId);

        boolean isCurrentlyIn = lastPunch.isPresent()
                && "In".equals(lastPunch.get().getDirection())
                && lastPunch.get().getTimestamp().toLocalDate().isEqual(today);

        // Get full day record for timestamps
        Optional<AttendanceRecord> record = attendanceRecordRepository
                .findByOrganizationIdAndEmployeeIdAndAttendanceDate(orgId, employeeId, today);

        Map<String, Object> result = new HashMap<>();
        result.put("currentlyCheckedIn", isCurrentlyIn);
        result.put("lastPunchDirection", lastPunch.map(AttendanceLog::getDirection).orElse(null));
        result.put("lastPunchTime", lastPunch.map(AttendanceLog::getTimestamp).orElse(null));
        record.ifPresent(r -> {
            result.put("firstCheckIn", r.getCheckIn());
            result.put("lastCheckOut", r.getCheckOut());
        });
        return result;
    }

    // ─────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceDashboardKPIs() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        LocalDate today = LocalDate.now();

        List<AttendanceRecord> todaysRecords = attendanceRecordRepository.findByOrganizationIdAndAttendanceDateBetween(orgId, today, today);
        long present = todaysRecords.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
        long absent = todaysRecords.stream().filter(r -> "Absent".equalsIgnoreCase(r.getStatus())).count();
        long late = todaysRecords.stream().filter(r -> "Late".equalsIgnoreCase(r.getStatus()) || (r.getLateBy() != null && r.getLateBy() > 0)).count();

        List<LeaveApplication> leaves = leaveApplicationRepository.findByOrganizationId(orgId);
        long onLeave = leaves.stream().filter(l -> "Approved".equalsIgnoreCase(l.getStatus())).count();
        long pendingRequests = leaves.stream().filter(l -> "Pending".equalsIgnoreCase(l.getStatus())).count();

        List<RegularizationRequest> regReqs = regularizationRequestRepository.findByOrganizationId(orgId);
        long regularization = regReqs.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("present", present);
        kpis.put("absent", absent);
        kpis.put("late", late);
        kpis.put("onLeave", onLeave);
        kpis.put("pendingRequests", pendingRequests);
        kpis.put("regularization", regularization);

        // Calculate real 7-day trend data
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDate startOfWeek = today.minusDays(6);
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            List<AttendanceRecord> dayRecs = attendanceRecordRepository.findByOrganizationIdAndAttendanceDateBetween(orgId, date, date);
            long dayPresent = dayRecs.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
            long dayAbsent = dayRecs.stream().filter(r -> "Absent".equalsIgnoreCase(r.getStatus())).count();

            Map<String, Object> t = new HashMap<>();
            t.put("name", date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            t.put("Present", dayPresent);
            t.put("Absent", dayAbsent);
            trendData.add(t);
        }
        kpis.put("trendData", trendData);

        // Leave category breakdown from real applications
        Map<String, Long> leaveCounts = leaves.stream()
                .filter(l -> l.getLeaveType() != null && l.getLeaveType().getName() != null)
                .collect(Collectors.groupingBy(l -> l.getLeaveType().getName(), Collectors.counting()));

        List<Map<String, Object>> leaveData = new ArrayList<>();
        if (leaveCounts.isEmpty()) {
            Map<String, Object> c = new HashMap<>(); c.put("name", "Casual"); c.put("value", 0);
            Map<String, Object> s = new HashMap<>(); s.put("name", "Sick"); s.put("value", 0);
            leaveData.add(c); leaveData.add(s);
        } else {
            leaveCounts.forEach((typeName, count) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", typeName);
                item.put("value", count);
                leaveData.add(item);
            });
        }
        kpis.put("leaveData", leaveData);

        return kpis;
    }
}
