package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.service.hrms.HrmsAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrms")
@RequiredArgsConstructor
public class HrmsAttendanceController {

    private final HrmsAttendanceService hrmsAttendanceService;

    // ─────────────────────────────────────────────────────────
    // SHIFTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/shifts")
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(hrmsAttendanceService.getAllShifts());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/shifts")
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        return ResponseEntity.ok(hrmsAttendanceService.createShift(shift));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/shifts/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable UUID id, @RequestBody Shift shift) {
        return ResponseEntity.ok(hrmsAttendanceService.updateShift(id, shift));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable UUID id) {
        hrmsAttendanceService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    // Rotations & Rosters
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/shifts/rotations")
    public ResponseEntity<List<ShiftRotationPattern>> getShiftRotationPatterns() {
        return ResponseEntity.ok(hrmsAttendanceService.getShiftRotationPatterns());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/shifts/rotations")
    public ResponseEntity<ShiftRotationPattern> createShiftRotationPattern(@RequestBody ShiftRotationPattern pattern) {
        return ResponseEntity.ok(hrmsAttendanceService.createShiftRotationPattern(pattern));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/shifts/rotations/{id}")
    public ResponseEntity<ShiftRotationPattern> updateShiftRotationPattern(@PathVariable UUID id, @RequestBody ShiftRotationPattern pattern) {
        return ResponseEntity.ok(hrmsAttendanceService.updateShiftRotationPattern(id, pattern));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/shifts/rotations/{id}")
    public ResponseEntity<Void> deleteShiftRotationPattern(@PathVariable UUID id) {
        hrmsAttendanceService.deleteShiftRotationPattern(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/shifts/roster")
    public ResponseEntity<List<ShiftRoster>> getShiftRosters() {
        return ResponseEntity.ok(hrmsAttendanceService.getShiftRosters());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/shifts/roster")
    public ResponseEntity<ShiftRoster> createShiftRoster(@RequestBody ShiftRoster roster) {
        return ResponseEntity.ok(hrmsAttendanceService.createShiftRoster(roster));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/shifts/roster/{id}")
    public ResponseEntity<ShiftRoster> updateShiftRoster(@PathVariable UUID id, @RequestBody ShiftRoster roster) {
        return ResponseEntity.ok(hrmsAttendanceService.updateShiftRoster(id, roster));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/shifts/roster/{id}")
    public ResponseEntity<Void> deleteShiftRoster(@PathVariable UUID id) {
        hrmsAttendanceService.deleteShiftRoster(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // HOLIDAY LISTS & HOLIDAYS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/holidays/lists")
    public ResponseEntity<List<HolidayList>> getHolidayLists() {
        return ResponseEntity.ok(hrmsAttendanceService.getHolidayLists());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/holidays/lists")
    public ResponseEntity<HolidayList> createHolidayList(@RequestBody HolidayList list) {
        return ResponseEntity.ok(hrmsAttendanceService.createHolidayList(list));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/holidays/lists/{id}")
    public ResponseEntity<HolidayList> updateHolidayList(@PathVariable UUID id, @RequestBody HolidayList list) {
        return ResponseEntity.ok(hrmsAttendanceService.updateHolidayList(id, list));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/holidays/lists/{id}")
    public ResponseEntity<Void> deleteHolidayList(@PathVariable UUID id) {
        hrmsAttendanceService.deleteHolidayList(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/holidays/lists/{listId}/holidays")
    public ResponseEntity<List<Holiday>> getHolidaysByListId(@PathVariable UUID listId) {
        return ResponseEntity.ok(hrmsAttendanceService.getHolidaysByListId(listId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/holidays/lists/{listId}/holidays")
    public ResponseEntity<Holiday> createHoliday(@PathVariable UUID listId, @RequestBody Holiday holiday) {
        return ResponseEntity.ok(hrmsAttendanceService.createHoliday(listId, holiday));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/holidays/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable UUID id, @RequestBody Holiday holiday) {
        return ResponseEntity.ok(hrmsAttendanceService.updateHoliday(id, holiday));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable UUID id) {
        hrmsAttendanceService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // SCHEMES, IP MAPPINGS, LOCK CONFIG
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance-config/schemes")
    public ResponseEntity<List<AttendanceScheme>> getAttendanceSchemes() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceSchemes());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance-config/schemes")
    public ResponseEntity<AttendanceScheme> createAttendanceScheme(@RequestBody AttendanceScheme scheme) {
        return ResponseEntity.ok(hrmsAttendanceService.createAttendanceScheme(scheme));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance-config/schemes/{id}")
    public ResponseEntity<AttendanceScheme> updateAttendanceScheme(@PathVariable UUID id, @RequestBody AttendanceScheme scheme) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendanceScheme(id, scheme));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance-config/schemes/{id}")
    public ResponseEntity<Void> deleteAttendanceScheme(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendanceScheme(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance-config/ip-mappings")
    public ResponseEntity<List<IpMapping>> getIpMappings() {
        return ResponseEntity.ok(hrmsAttendanceService.getIpMappings());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance-config/ip-mappings")
    public ResponseEntity<IpMapping> createIpMapping(@RequestBody IpMapping mapping) {
        return ResponseEntity.ok(hrmsAttendanceService.createIpMapping(mapping));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance-config/ip-mappings/{id}")
    public ResponseEntity<IpMapping> updateIpMapping(@PathVariable UUID id, @RequestBody IpMapping mapping) {
        return ResponseEntity.ok(hrmsAttendanceService.updateIpMapping(id, mapping));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance-config/ip-mappings/{id}")
    public ResponseEntity<Void> deleteIpMapping(@PathVariable UUID id) {
        hrmsAttendanceService.deleteIpMapping(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance-config/lock-config")
    public ResponseEntity<List<LockConfiguration>> getLockConfigurations() {
        return ResponseEntity.ok(hrmsAttendanceService.getLockConfigurations());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance-config/lock-config")
    public ResponseEntity<LockConfiguration> createLockConfiguration(@RequestBody LockConfiguration config) {
        return ResponseEntity.ok(hrmsAttendanceService.createLockConfiguration(config));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance-config/lock-config/{id}")
    public ResponseEntity<LockConfiguration> updateLockConfiguration(@PathVariable UUID id, @RequestBody LockConfiguration config) {
        return ResponseEntity.ok(hrmsAttendanceService.updateLockConfiguration(id, config));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance-config/lock-config/{id}")
    public ResponseEntity<Void> deleteLockConfiguration(@PathVariable UUID id) {
        hrmsAttendanceService.deleteLockConfiguration(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // LEAVE TYPES & SCHEMES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leave/types")
    public ResponseEntity<List<LeaveType>> getLeaveTypes() {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveTypes());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/leave/types")
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveType type) {
        return ResponseEntity.ok(hrmsAttendanceService.createLeaveType(type));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/types/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable UUID id, @RequestBody LeaveType type) {
        return ResponseEntity.ok(hrmsAttendanceService.updateLeaveType(id, type));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/leave/types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable UUID id) {
        hrmsAttendanceService.deleteLeaveType(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leave/schemes")
    public ResponseEntity<List<LeaveScheme>> getLeaveSchemes() {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveSchemes());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/leave/schemes")
    public ResponseEntity<LeaveScheme> createLeaveScheme(@RequestBody LeaveScheme scheme) {
        return ResponseEntity.ok(hrmsAttendanceService.createLeaveScheme(scheme));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/schemes/{id}")
    public ResponseEntity<LeaveScheme> updateLeaveScheme(@PathVariable UUID id, @RequestBody LeaveScheme scheme) {
        return ResponseEntity.ok(hrmsAttendanceService.updateLeaveScheme(id, scheme));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/leave/schemes/{id}")
    public ResponseEntity<Void> deleteLeaveScheme(@PathVariable UUID id) {
        hrmsAttendanceService.deleteLeaveScheme(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // LEAVE BALANCES & APPLICATIONS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leave/balances")
    public ResponseEntity<List<LeaveBalance>> getLeaveBalances(@RequestParam(required = false) UUID employeeId) {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveBalances(employeeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leave/applications")
    public ResponseEntity<List<LeaveApplication>> getLeaveApplications() {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveApplications());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/leave/applications")
    public ResponseEntity<LeaveApplication> createLeaveApplication(@RequestBody LeaveApplication application) {
        return ResponseEntity.ok(hrmsAttendanceService.createLeaveApplication(application));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/applications/{id}/approve")
    public ResponseEntity<LeaveApplication> approveLeaveApplication(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.approveLeaveApplication(id, remarks));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/applications/{id}/reject")
    public ResponseEntity<LeaveApplication> rejectLeaveApplication(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.rejectLeaveApplication(id, remarks));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/applications/{id}/cancel")
    public ResponseEntity<LeaveApplication> cancelLeaveApplication(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(hrmsAttendanceService.cancelLeaveApplication(id, reason));
    }

    // ─────────────────────────────────────────────────────────
    // ATTENDANCE RECORDS & CHECK-IN/OUT
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/records")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceRecords() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceRecords());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records")
    public ResponseEntity<AttendanceRecord> createAttendanceRecord(@RequestBody AttendanceRecord record) {
        return ResponseEntity.ok(hrmsAttendanceService.createOrUpdateRecord(record));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records/check-in")
    public ResponseEntity<AttendanceRecord> checkIn(@RequestBody Map<String, String> body) {
        UUID employeeId = UUID.fromString(body.get("employeeId"));
        String source = body.get("source");
        return ResponseEntity.ok(hrmsAttendanceService.checkIn(employeeId, source));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records/check-out")
    public ResponseEntity<AttendanceRecord> checkOut(@RequestBody Map<String, String> body) {
        UUID employeeId = UUID.fromString(body.get("employeeId"));
        return ResponseEntity.ok(hrmsAttendanceService.checkOut(employeeId));
    }

    // Regularizations
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/regularizations")
    public ResponseEntity<List<RegularizationRequest>> getRegularizationRequests() {
        return ResponseEntity.ok(hrmsAttendanceService.getRegularizationRequests());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/regularizations")
    public ResponseEntity<RegularizationRequest> createRegularizationRequest(@RequestBody RegularizationRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.createRegularizationRequest(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/regularizations/{id}/approve")
    public ResponseEntity<RegularizationRequest> approveRegularizationRequest(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.approveRegularizationRequest(id, remarks));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/regularizations/{id}/reject")
    public ResponseEntity<RegularizationRequest> rejectRegularizationRequest(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.rejectRegularizationRequest(id, remarks));
    }

    // Permissions
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/permissions")
    public ResponseEntity<List<PermissionRequest>> getPermissionRequests() {
        return ResponseEntity.ok(hrmsAttendanceService.getPermissionRequests());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/permissions")
    public ResponseEntity<PermissionRequest> createPermissionRequest(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.createPermissionRequest(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/permissions/{id}/approve")
    public ResponseEntity<PermissionRequest> approvePermissionRequest(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.approvePermissionRequest(id, remarks));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/permissions/{id}/reject")
    public ResponseEntity<PermissionRequest> rejectPermissionRequest(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.get("remarks") : null;
        return ResponseEntity.ok(hrmsAttendanceService.rejectPermissionRequest(id, remarks));
    }

    // Exceptions, Logs, Devices
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/exceptions")
    public ResponseEntity<List<AttendanceException>> getAttendanceExceptions() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceExceptions());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/exceptions/{id}/resolve")
    public ResponseEntity<AttendanceException> resolveAttendanceException(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.resolveAttendanceException(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/logs")
    public ResponseEntity<List<AttendanceLog>> getAttendanceLogs() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceLogs());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/logs")
    public ResponseEntity<AttendanceLog> ingestAttendanceLog(@RequestBody AttendanceLog log) {
        return ResponseEntity.ok(hrmsAttendanceService.ingestAttendanceLog(log));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/devices")
    public ResponseEntity<List<AttendanceDevice>> getAttendanceDevices() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceDevices());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/devices")
    public ResponseEntity<AttendanceDevice> createAttendanceDevice(@RequestBody AttendanceDevice device) {
        return ResponseEntity.ok(hrmsAttendanceService.createAttendanceDevice(device));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/devices/{id}")
    public ResponseEntity<AttendanceDevice> updateAttendanceDevice(@PathVariable UUID id, @RequestBody AttendanceDevice device) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendanceDevice(id, device));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/devices/{id}")
    public ResponseEntity<Void> deleteAttendanceDevice(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendanceDevice(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // PROCESSING & PERIODS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/processing/periods")
    public ResponseEntity<List<AttendancePeriod>> getAttendancePeriods() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendancePeriods());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/processing/periods")
    public ResponseEntity<AttendancePeriod> createAttendancePeriod(@RequestBody AttendancePeriod period) {
        return ResponseEntity.ok(hrmsAttendanceService.createAttendancePeriod(period));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/processing/periods/{id}/lock")
    public ResponseEntity<AttendancePeriod> lockAttendancePeriod(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.lockAttendancePeriod(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/processing/periods/{id}/process")
    public ResponseEntity<AttendancePeriod> processAttendancePeriod(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.processAttendancePeriod(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/processing/periods/{periodId}/processed")
    public ResponseEntity<List<ProcessedAttendance>> getProcessedAttendanceByPeriodId(@PathVariable UUID periodId) {
        return ResponseEntity.ok(hrmsAttendanceService.getProcessedAttendanceByPeriodId(periodId));
    }

    // ─────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/dashboard/kpis")
    public ResponseEntity<Map<String, Object>> getAttendanceDashboardKPIs() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceDashboardKPIs());
    }
}
