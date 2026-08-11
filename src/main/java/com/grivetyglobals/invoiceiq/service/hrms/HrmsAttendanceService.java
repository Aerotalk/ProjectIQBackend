package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.*;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
                    .performedBy("System User")
                    .performedOn(LocalDateTime.now())
                    .remarks(remarks)
                    .build();
            approvalHistoryRepository.save(history);
        } catch (Exception ignored) {}
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
        return shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
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
        return shiftRotationPatternRepository.findById(id).orElseThrow(() -> new RuntimeException("Rotation pattern not found"));
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
        return shiftRosterRepository.findById(id).orElseThrow(() -> new RuntimeException("Roster not found"));
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
        return holidayListRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday list not found"));
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
        return holidayRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday not found"));
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
        return attendanceSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scheme not found"));
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
        attendanceSchemeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<IpMapping> getIpMappings() {
        return ipMappingRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public IpMapping getIpMappingById(UUID id) {
        return ipMappingRepository.findById(id).orElseThrow(() -> new RuntimeException("IP Mapping not found"));
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
        ipMappingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LockConfiguration> getLockConfigurations() {
        return lockConfigurationRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public LockConfiguration getLockConfigurationById(UUID id) {
        return lockConfigurationRepository.findById(id).orElseThrow(() -> new RuntimeException("Lock config not found"));
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
        return leaveTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave type not found"));
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
        leaveTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LeaveScheme> getLeaveSchemes() {
        return leaveSchemeRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public LeaveScheme getLeaveSchemeById(UUID id) {
        return leaveSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave scheme not found"));
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
        leaveSchemeRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // LEAVE BALANCES & APPLICATIONS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LeaveBalance> getLeaveBalances(UUID employeeId) {
        if (employeeId != null) {
            return leaveBalanceRepository.findByEmployeeId(employeeId);
        }
        return leaveBalanceRepository.findAll();
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
        return leaveApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
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
        return attendanceRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Attendance record not found"));
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
        attendanceRecordRepository.deleteById(id);
    }

    @Transactional
    public AttendanceRecord checkIn(UUID employeeId, String source) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
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
        return regularizationRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
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
        return permissionRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
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
        return attendanceExceptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception not found"));
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
        return attendanceLogRepository.findById(id).orElseThrow(() -> new RuntimeException("Log not found"));
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
        attendanceLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDevice> getAttendanceDevices() {
        return attendanceDeviceRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public AttendanceDevice getAttendanceDeviceById(UUID id) {
        return attendanceDeviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
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
        return attendancePeriodRepository.findById(id).orElseThrow(() -> new RuntimeException("Period not found"));
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
        return processedAttendanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Processed attendance not found"));
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
        return approvalHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Approval history not found"));
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
