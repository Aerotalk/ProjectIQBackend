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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;

    private Organization getCurrentOrganization() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    // ─────────────────────────────────────────────────────────
    // SHIFTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Shift> getAllShifts() {
        return shiftRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public Shift createShift(Shift shift) {
        shift.setOrganization(getCurrentOrganization());
        if (shift.getActive() == null) shift.setActive(true);
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift updateShift(UUID id, Shift updated) {
        Shift shift = shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
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

    @Transactional
    public ShiftRotationPattern createShiftRotationPattern(ShiftRotationPattern pattern) {
        pattern.setOrganization(getCurrentOrganization());
        return shiftRotationPatternRepository.save(pattern);
    }

    @Transactional
    public ShiftRotationPattern updateShiftRotationPattern(UUID id, ShiftRotationPattern updated) {
        ShiftRotationPattern pattern = shiftRotationPatternRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rotation pattern not found"));
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

    @Transactional
    public ShiftRoster createShiftRoster(ShiftRoster roster) {
        roster.setOrganization(getCurrentOrganization());
        if (roster.getPublished() == null) roster.setPublished(false);
        if (roster.getOverridden() == null) roster.setOverridden(false);
        return shiftRosterRepository.save(roster);
    }

    @Transactional
    public ShiftRoster updateShiftRoster(UUID id, ShiftRoster updated) {
        ShiftRoster roster = shiftRosterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roster not found"));
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

    @Transactional
    public HolidayList createHolidayList(HolidayList list) {
        list.setOrganization(getCurrentOrganization());
        return holidayListRepository.save(list);
    }

    @Transactional
    public HolidayList updateHolidayList(UUID id, HolidayList updated) {
        HolidayList list = holidayListRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday list not found"));
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

    @Transactional
    public Holiday createHoliday(UUID listId, Holiday holiday) {
        HolidayList list = holidayListRepository.findById(listId).orElseThrow(() -> new RuntimeException("Holiday list not found"));
        holiday.setHolidayList(list);
        if (holiday.getActive() == null) holiday.setActive(true);
        return holidayRepository.save(holiday);
    }

    @Transactional
    public Holiday updateHoliday(UUID id, Holiday updated) {
        Holiday holiday = holidayRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday not found"));
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

    @Transactional
    public AttendanceScheme createAttendanceScheme(AttendanceScheme scheme) {
        scheme.setOrganization(getCurrentOrganization());
        return attendanceSchemeRepository.save(scheme);
    }

    @Transactional
    public AttendanceScheme updateAttendanceScheme(UUID id, AttendanceScheme updated) {
        AttendanceScheme scheme = attendanceSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scheme not found"));
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

    @Transactional
    public IpMapping createIpMapping(IpMapping mapping) {
        mapping.setOrganization(getCurrentOrganization());
        return ipMappingRepository.save(mapping);
    }

    @Transactional
    public IpMapping updateIpMapping(UUID id, IpMapping updated) {
        IpMapping mapping = ipMappingRepository.findById(id).orElseThrow(() -> new RuntimeException("IP Mapping not found"));
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

    @Transactional
    public LockConfiguration createLockConfiguration(LockConfiguration config) {
        config.setOrganization(getCurrentOrganization());
        return lockConfigurationRepository.save(config);
    }

    @Transactional
    public LockConfiguration updateLockConfiguration(UUID id, LockConfiguration updated) {
        LockConfiguration config = lockConfigurationRepository.findById(id).orElseThrow(() -> new RuntimeException("Lock config not found"));
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

    @Transactional
    public LeaveType createLeaveType(LeaveType type) {
        type.setOrganization(getCurrentOrganization());
        if (type.getActive() == null) type.setActive(true);
        return leaveTypeRepository.save(type);
    }

    @Transactional
    public LeaveType updateLeaveType(UUID id, LeaveType updated) {
        LeaveType type = leaveTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave type not found"));
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

    @Transactional
    public LeaveScheme createLeaveScheme(LeaveScheme scheme) {
        scheme.setOrganization(getCurrentOrganization());
        return leaveSchemeRepository.save(scheme);
    }

    @Transactional
    public LeaveScheme updateLeaveScheme(UUID id, LeaveScheme updated) {
        LeaveScheme scheme = leaveSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave scheme not found"));
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
    public List<LeaveApplication> getLeaveApplications() {
        return leaveApplicationRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
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
    public LeaveApplication approveLeaveApplication(UUID id, String remarks) {
        LeaveApplication app = leaveApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus("Approved");
        app.setApprovalRemarks(remarks);
        app.setApprovedAt(LocalDateTime.now());

        // Deduct from leave balance
        int currentYear = app.getFromDate().getYear();
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(app.getEmployee().getId(), app.getLeaveType().getId(), currentYear)
                .ifPresent(bal -> {
                    BigDecimal availed = bal.getAvailed() != null ? bal.getAvailed() : BigDecimal.ZERO;
                    BigDecimal duration = app.getDuration() != null ? app.getDuration() : BigDecimal.ONE;
                    bal.setAvailed(availed.add(duration));
                    BigDecimal avail = bal.getAvailable() != null ? bal.getAvailable() : BigDecimal.ZERO;
                    bal.setAvailable(avail.subtract(duration));
                    leaveBalanceRepository.save(bal);
                });

        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public LeaveApplication rejectLeaveApplication(UUID id, String remarks) {
        LeaveApplication app = leaveApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus("Rejected");
        app.setApprovalRemarks(remarks);
        app.setApprovedAt(LocalDateTime.now());
        return leaveApplicationRepository.save(app);
    }

    @Transactional
    public LeaveApplication cancelLeaveApplication(UUID id, String reason) {
        LeaveApplication app = leaveApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus("Cancelled");
        app.setCancelReason(reason);
        return leaveApplicationRepository.save(app);
    }

    // ─────────────────────────────────────────────────────────
    // ATTENDANCE RECORDS, CHECK-IN/OUT
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecordRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public AttendanceRecord createOrUpdateRecord(AttendanceRecord rec) {
        rec.setOrganization(getCurrentOrganization());
        return attendanceRecordRepository.save(rec);
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
    public RegularizationRequest approveRegularizationRequest(UUID id, String remarks) {
        RegularizationRequest req = regularizationRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("Approved");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());

        // Update corresponding attendance record if exists
        attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(req.getEmployee().getId(), req.getDate())
                .ifPresent(rec -> {
                    rec.setRegularized(true);
                    rec.setRegularizationStatus("Approved");
                    attendanceRecordRepository.save(rec);
                });

        return regularizationRequestRepository.save(req);
    }

    @Transactional
    public RegularizationRequest rejectRegularizationRequest(UUID id, String remarks) {
        RegularizationRequest req = regularizationRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("Rejected");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        return regularizationRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public List<PermissionRequest> getPermissionRequests() {
        return permissionRequestRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
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
    public PermissionRequest approvePermissionRequest(UUID id, String remarks) {
        PermissionRequest req = permissionRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("Approved");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        return permissionRequestRepository.save(req);
    }

    @Transactional
    public PermissionRequest rejectPermissionRequest(UUID id, String remarks) {
        PermissionRequest req = permissionRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("Rejected");
        req.setApprovalRemarks(remarks);
        req.setApprovedAt(LocalDateTime.now());
        return permissionRequestRepository.save(req);
    }

    // ─────────────────────────────────────────────────────────
    // EXCEPTIONS, LOGS, DEVICES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceException> getAttendanceExceptions() {
        return attendanceExceptionRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public AttendanceException resolveAttendanceException(UUID id) {
        AttendanceException exc = attendanceExceptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception not found"));
        exc.setResolved(true);
        exc.setResolvedAt(LocalDateTime.now());
        return attendanceExceptionRepository.save(exc);
    }

    @Transactional(readOnly = true)
    public List<AttendanceLog> getAttendanceLogs() {
        return attendanceLogRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public AttendanceLog ingestAttendanceLog(AttendanceLog log) {
        log.setOrganization(getCurrentOrganization());
        if (log.getTimestamp() == null) log.setTimestamp(LocalDateTime.now());
        return attendanceLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDevice> getAttendanceDevices() {
        return attendanceDeviceRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public AttendanceDevice createAttendanceDevice(AttendanceDevice device) {
        device.setOrganization(getCurrentOrganization());
        if (device.getStatus() == null) device.setStatus("Active");
        return attendanceDeviceRepository.save(device);
    }

    @Transactional
    public AttendanceDevice updateAttendanceDevice(UUID id, AttendanceDevice updated) {
        AttendanceDevice device = attendanceDeviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
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

    @Transactional
    public AttendancePeriod createAttendancePeriod(AttendancePeriod period) {
        period.setOrganization(getCurrentOrganization());
        if (period.getStatus() == null) period.setStatus("Open");
        if (period.getProcessingStatus() == null) period.setProcessingStatus("Open");
        return attendancePeriodRepository.save(period);
    }

    @Transactional
    public AttendancePeriod lockAttendancePeriod(UUID id) {
        AttendancePeriod period = attendancePeriodRepository.findById(id).orElseThrow(() -> new RuntimeException("Period not found"));
        period.setStatus("Locked");
        period.setProcessingStatus("Locked");
        period.setLockedAt(LocalDateTime.now());
        return attendancePeriodRepository.save(period);
    }

    @Transactional
    public AttendancePeriod processAttendancePeriod(UUID id) {
        AttendancePeriod period = attendancePeriodRepository.findById(id).orElseThrow(() -> new RuntimeException("Period not found"));
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
    public java.util.Map<String, Object> getAttendanceDashboardKPIs() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        LocalDate today = LocalDate.now();

        List<AttendanceRecord> todaysRecords = attendanceRecordRepository.findByOrganizationIdAndAttendanceDateBetween(orgId, today, today);
        long present = todaysRecords.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
        long absent = todaysRecords.stream().filter(r -> "Absent".equalsIgnoreCase(r.getStatus())).count();
        long late = todaysRecords.stream().filter(r -> "Late".equalsIgnoreCase(r.getStatus())).count();

        List<LeaveApplication> leaves = leaveApplicationRepository.findByOrganizationId(orgId);
        long onLeave = leaves.stream().filter(l -> "Approved".equalsIgnoreCase(l.getStatus())).count();
        long pendingRequests = leaves.stream().filter(l -> "Pending".equalsIgnoreCase(l.getStatus())).count();

        List<RegularizationRequest> regReqs = regularizationRequestRepository.findByOrganizationId(orgId);
        long regularization = regReqs.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();

        java.util.Map<String, Object> kpis = new java.util.HashMap<>();
        kpis.put("present", present);
        kpis.put("absent", absent);
        kpis.put("late", late);
        kpis.put("onLeave", onLeave);
        kpis.put("pendingRequests", pendingRequests);
        kpis.put("regularization", regularization);

        // Mock trendData for frontend charts (to replace hardcoded mock on frontend)
        List<java.util.Map<String, Object>> trendData = new java.util.ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            java.util.Map<String, Object> t = new java.util.HashMap<>();
            t.put("name", day);
            t.put("Present", 120 - (int)(Math.random() * 20));
            t.put("Absent", (int)(Math.random() * 10));
            trendData.add(t);
        }
        kpis.put("trendData", trendData);

        // Mock leaveData for frontend charts
        List<java.util.Map<String, Object>> leaveData = new java.util.ArrayList<>();
        java.util.Map<String, Object> c = new java.util.HashMap<>(); c.put("name", "Casual"); c.put("value", 400);
        java.util.Map<String, Object> s = new java.util.HashMap<>(); s.put("name", "Sick"); s.put("value", 300);
        java.util.Map<String, Object> l = new java.util.HashMap<>(); l.put("name", "LOP"); l.put("value", 100);
        leaveData.add(c); leaveData.add(s); leaveData.add(l);
        kpis.put("leaveData", leaveData);

        return kpis;
    }
}
