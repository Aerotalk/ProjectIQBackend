package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.entity.hrms.ApprovalHistory;
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
    @GetMapping("/shifts/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getShiftById(id));
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
    @GetMapping("/shifts/rotations/{id}")
    public ResponseEntity<ShiftRotationPattern> getShiftRotationPatternById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getShiftRotationPatternById(id));
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
    @GetMapping("/shifts/roster/{id}")
    public ResponseEntity<ShiftRoster> getShiftRosterById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getShiftRosterById(id));
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
    @GetMapping("/holidays/lists/{id}")
    public ResponseEntity<HolidayList> getHolidayListById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getHolidayListById(id));
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
    @GetMapping("/holidays")
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        return ResponseEntity.ok(hrmsAttendanceService.getAllHolidays());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/holidays/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getHolidayById(id));
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
    @PostMapping("/holidays")
    public ResponseEntity<Holiday> createHolidayDirect(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(hrmsAttendanceService.createHolidayDirect(holiday));
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
    @GetMapping("/attendance-config/schemes/{id}")
    public ResponseEntity<AttendanceScheme> getAttendanceSchemeById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceSchemeById(id));
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
    @GetMapping("/attendance-config/ip-mappings/{id}")
    public ResponseEntity<IpMapping> getIpMappingById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getIpMappingById(id));
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
    @GetMapping("/attendance-config/lock-config/{id}")
    public ResponseEntity<LockConfiguration> getLockConfigurationById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getLockConfigurationById(id));
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
    @GetMapping("/leave/types/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveTypeById(id));
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
    @GetMapping("/leave/schemes/{id}")
    public ResponseEntity<LeaveScheme> getLeaveSchemeById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveSchemeById(id));
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
    public ResponseEntity<List<LeaveApplication>> getLeaveApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID employeeId) {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveApplicationsFiltered(status, employeeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leave/applications/{id}")
    public ResponseEntity<LeaveApplication> getLeaveApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getLeaveApplicationById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/leave/applications")
    public ResponseEntity<LeaveApplication> createLeaveApplication(@RequestBody LeaveApplication application) {
        return ResponseEntity.ok(hrmsAttendanceService.createLeaveApplication(application));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave/applications/{id}")
    public ResponseEntity<LeaveApplication> updateLeaveApplication(@PathVariable UUID id, @RequestBody LeaveApplication application) {
        return ResponseEntity.ok(hrmsAttendanceService.updateLeaveApplication(id, application));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/leave/applications/{id}")
    public ResponseEntity<Void> deleteLeaveApplication(@PathVariable UUID id) {
        hrmsAttendanceService.deleteLeaveApplication(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<AttendanceRecord>> getAttendanceRecords(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceRecordsFiltered(status, employeeId, startDate, endDate));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/records/{id}")
    public ResponseEntity<AttendanceRecord> getAttendanceRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceRecordById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records")
    public ResponseEntity<AttendanceRecord> createAttendanceRecord(@RequestBody AttendanceRecord record) {
        return ResponseEntity.ok(hrmsAttendanceService.createOrUpdateRecord(record));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/records/{id}")
    public ResponseEntity<AttendanceRecord> updateAttendanceRecord(@PathVariable UUID id, @RequestBody AttendanceRecord record) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendanceRecord(id, record));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/records/{id}")
    public ResponseEntity<Void> deleteAttendanceRecord(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendanceRecord(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records/check-in")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Object> body) {
        String empIdStr = body.get("employeeId") != null ? body.get("employeeId").toString() : null;
        if (empIdStr == null || empIdStr.isBlank()) {
            return ResponseEntity.badRequest().body("employeeId is required");
        }
        UUID employeeId;
        try {
            employeeId = UUID.fromString(empIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid employeeId format");
        }
        String source = body.get("source") != null ? body.get("source").toString() : null;
        java.math.BigDecimal latitude = body.get("latitude") != null ? new java.math.BigDecimal(body.get("latitude").toString()) : null;
        java.math.BigDecimal longitude = body.get("longitude") != null ? new java.math.BigDecimal(body.get("longitude").toString()) : null;
        String locationLabel = body.get("locationLabel") != null ? body.get("locationLabel").toString() : null;
        return ResponseEntity.ok(hrmsAttendanceService.checkIn(employeeId, source, latitude, longitude, locationLabel));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/records/check-in/status")
    public ResponseEntity<Map<String, Object>> getCheckInStatus(@RequestParam UUID employeeId) {
        return ResponseEntity.ok(hrmsAttendanceService.getCheckInStatus(employeeId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/records/check-out")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Object> body) {
        String empIdStr = body.get("employeeId") != null ? body.get("employeeId").toString() : null;
        if (empIdStr == null || empIdStr.isBlank()) {
            return ResponseEntity.badRequest().body("employeeId is required");
        }
        UUID employeeId;
        try {
            employeeId = UUID.fromString(empIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid employeeId format");
        }
        java.math.BigDecimal latitude = body.get("latitude") != null ? new java.math.BigDecimal(body.get("latitude").toString()) : null;
        java.math.BigDecimal longitude = body.get("longitude") != null ? new java.math.BigDecimal(body.get("longitude").toString()) : null;
        String locationLabel = body.get("locationLabel") != null ? body.get("locationLabel").toString() : null;
        return ResponseEntity.ok(hrmsAttendanceService.checkOut(employeeId, latitude, longitude, locationLabel));
    }

    // Regularizations
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/regularizations")
    public ResponseEntity<List<RegularizationRequest>> getRegularizationRequests(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(hrmsAttendanceService.getRegularizationRequestsFiltered(status));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/regularizations/{id}")
    public ResponseEntity<RegularizationRequest> getRegularizationRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getRegularizationRequestById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/regularizations")
    public ResponseEntity<RegularizationRequest> createRegularizationRequest(@RequestBody RegularizationRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.createRegularizationRequest(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/regularizations/{id}")
    public ResponseEntity<RegularizationRequest> updateRegularizationRequest(@PathVariable UUID id, @RequestBody RegularizationRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.updateRegularizationRequest(id, request));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/regularizations/{id}")
    public ResponseEntity<Void> deleteRegularizationRequest(@PathVariable UUID id) {
        hrmsAttendanceService.deleteRegularizationRequest(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<PermissionRequest>> getPermissionRequests(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(hrmsAttendanceService.getPermissionRequestsFiltered(status));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/permissions/{id}")
    public ResponseEntity<PermissionRequest> getPermissionRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getPermissionRequestById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/permissions")
    public ResponseEntity<PermissionRequest> createPermissionRequest(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.createPermissionRequest(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/permissions/{id}")
    public ResponseEntity<PermissionRequest> updatePermissionRequest(@PathVariable UUID id, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(hrmsAttendanceService.updatePermissionRequest(id, request));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/permissions/{id}")
    public ResponseEntity<Void> deletePermissionRequest(@PathVariable UUID id) {
        hrmsAttendanceService.deletePermissionRequest(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<AttendanceException>> getAttendanceExceptions(@RequestParam(required = false) Boolean resolved) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceExceptionsFiltered(resolved));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/exceptions/{id}")
    public ResponseEntity<AttendanceException> getAttendanceExceptionById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceExceptionById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/exceptions")
    public ResponseEntity<AttendanceException> createAttendanceException(@RequestBody AttendanceException exception) {
        return ResponseEntity.ok(hrmsAttendanceService.createAttendanceException(exception));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/exceptions/{id}")
    public ResponseEntity<AttendanceException> updateAttendanceException(@PathVariable UUID id, @RequestBody AttendanceException exception) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendanceException(id, exception));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/exceptions/{id}")
    public ResponseEntity<Void> deleteAttendanceException(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendanceException(id);
        return ResponseEntity.noContent().build();
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
    @GetMapping("/attendance/logs/{id}")
    public ResponseEntity<AttendanceLog> getAttendanceLogById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceLogById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/logs")
    public ResponseEntity<AttendanceLog> ingestAttendanceLog(@RequestBody AttendanceLog log) {
        return ResponseEntity.ok(hrmsAttendanceService.ingestAttendanceLog(log));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/logs/{id}")
    public ResponseEntity<AttendanceLog> updateAttendanceLog(@PathVariable UUID id, @RequestBody AttendanceLog log) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendanceLog(id, log));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/logs/{id}")
    public ResponseEntity<Void> deleteAttendanceLog(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendanceLog(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/devices")
    public ResponseEntity<List<AttendanceDevice>> getAttendanceDevices() {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceDevices());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/devices/{id}")
    public ResponseEntity<AttendanceDevice> getAttendanceDeviceById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendanceDeviceById(id));
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
    @GetMapping("/attendance/processing/periods/{id}")
    public ResponseEntity<AttendancePeriod> getAttendancePeriodById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getAttendancePeriodById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/processing/periods")
    public ResponseEntity<AttendancePeriod> createAttendancePeriod(@RequestBody AttendancePeriod period) {
        return ResponseEntity.ok(hrmsAttendanceService.createAttendancePeriod(period));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/processing/periods/{id}")
    public ResponseEntity<AttendancePeriod> updateAttendancePeriod(@PathVariable UUID id, @RequestBody AttendancePeriod period) {
        return ResponseEntity.ok(hrmsAttendanceService.updateAttendancePeriod(id, period));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/processing/periods/{id}")
    public ResponseEntity<Void> deleteAttendancePeriod(@PathVariable UUID id) {
        hrmsAttendanceService.deleteAttendancePeriod(id);
        return ResponseEntity.noContent().build();
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/processing/processed/{id}")
    public ResponseEntity<ProcessedAttendance> getProcessedAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getProcessedAttendanceById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/attendance/processing/processed")
    public ResponseEntity<ProcessedAttendance> createProcessedAttendance(@RequestBody ProcessedAttendance pa) {
        return ResponseEntity.ok(hrmsAttendanceService.createProcessedAttendance(pa));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/attendance/processing/processed/{id}")
    public ResponseEntity<ProcessedAttendance> updateProcessedAttendance(@PathVariable UUID id, @RequestBody ProcessedAttendance pa) {
        return ResponseEntity.ok(hrmsAttendanceService.updateProcessedAttendance(id, pa));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/attendance/processing/processed/{id}")
    public ResponseEntity<Void> deleteProcessedAttendance(@PathVariable UUID id) {
        hrmsAttendanceService.deleteProcessedAttendance(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // APPROVAL HISTORY
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/approval-history")
    public ResponseEntity<List<ApprovalHistory>> getApprovalHistories() {
        return ResponseEntity.ok(hrmsAttendanceService.getApprovalHistories());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/approval-history/{id}")
    public ResponseEntity<ApprovalHistory> getApprovalHistoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getApprovalHistoryById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/approval-history")
    public ResponseEntity<ApprovalHistory> createApprovalHistory(@RequestBody ApprovalHistory history) {
        return ResponseEntity.ok(hrmsAttendanceService.createApprovalHistory(history));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/approval-history/{id}")
    public ResponseEntity<ApprovalHistory> updateApprovalHistory(@PathVariable UUID id, @RequestBody ApprovalHistory history) {
        return ResponseEntity.ok(hrmsAttendanceService.updateApprovalHistory(id, history));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/approval-history/{id}")
    public ResponseEntity<Void> deleteApprovalHistory(@PathVariable UUID id) {
        hrmsAttendanceService.deleteApprovalHistory(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // ATTENDANCE SUMMARIES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/summaries")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeAttendanceSummaries(@RequestParam(required = false) UUID employeeId) {
        return ResponseEntity.ok(hrmsAttendanceService.getEmployeeAttendanceSummaries(employeeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/attendance/summaries/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeAttendanceSummaryById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsAttendanceService.getEmployeeAttendanceSummaryById(id));
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
