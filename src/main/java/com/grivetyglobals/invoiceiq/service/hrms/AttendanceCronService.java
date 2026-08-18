package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceRecord;
import com.grivetyglobals.invoiceiq.entity.hrms.Holiday;
import com.grivetyglobals.invoiceiq.entity.hrms.LeaveApplication;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.AttendanceRecordRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.HolidayRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceCronService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final HolidayRepository holidayRepository;

    /**
     * Runs every day at 23:59:00 to evaluate absent/leave/holiday logic for all employees.
     */
    @Scheduled(cron = "0 59 23 * * ?")
    @Transactional
    public void processDailyAttendance() {
        log.info("Starting nightly attendance processing...");
        LocalDate today = LocalDate.now();

        // 1. Get all employees in the system
        List<Employee> allEmployees = employeeRepository.findAll();

        for (Employee employee : allEmployees) {
            if (employee.getOrganization() == null) continue;
            UUID orgId = employee.getOrganization().getId();

            // 2. Check if an AttendanceRecord already exists for today
            Optional<AttendanceRecord> existingRecord = attendanceRecordRepository
                    .findByOrganizationIdAndEmployeeIdAndAttendanceDate(orgId, employee.getId(), today);

            if (existingRecord.isEmpty()) {
                // Determine the correct status
                String statusToApply = determineUnpunchedStatus(employee, orgId, today);

                // Create the record
                AttendanceRecord record = AttendanceRecord.builder()
                        .organization(employee.getOrganization())
                        .employee(employee)
                        .attendanceDate(today)
                        .status(statusToApply)
                        .attendanceSource("System")
                        .build();

                attendanceRecordRepository.save(record);
                log.debug("Created {} record for employee {}", statusToApply, employee.getId());
            }
        }
        log.info("Finished nightly attendance processing.");
    }

    private String determineUnpunchedStatus(Employee employee, UUID orgId, LocalDate date) {
        // 1. Check for Approved Leaves
        List<LeaveApplication> leaves = leaveApplicationRepository.findByEmployeeIdAndStatus(employee.getId(), "Approved");
        for (LeaveApplication leave : leaves) {
            if (!date.isBefore(leave.getFromDate()) && !date.isAfter(leave.getToDate())) {
                return "Leave";
            }
        }

        // 2. Check for Holidays
        List<Holiday> holidays = holidayRepository.findByHolidayListOrganizationId(orgId);
        for (Holiday holiday : holidays) {
            if (date.equals(holiday.getHolidayDate()) && holiday.getActive() != null && holiday.getActive()) {
                return "Holiday";
            }
        }

        // 3. Check for Weekends
        String dayOfWeek = date.getDayOfWeek().name();
        // Simple logic: if today's name is in their weeklyOff string
        if (employee.getWeeklyOff() != null && employee.getWeeklyOff().toUpperCase().contains(dayOfWeek)) {
            return "Weekend";
        }
        // Default standard weekends if not defined in employee
        if (employee.getWeeklyOff() == null && (dayOfWeek.equals("SATURDAY") || dayOfWeek.equals("SUNDAY"))) {
            return "Weekend";
        }

        // 4. Default to Absent
        return "Absent";
    }
}
