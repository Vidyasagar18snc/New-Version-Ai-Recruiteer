package com.Vendor.service;

import com.Vendor.model.Attendance;
import com.Vendor.model.Employee;
import com.Vendor.repository.AttendanceRepository;
import com.Vendor.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final EmployeeRepository employeeRepository;

    // CHECK IN

    public String checkIn(String employeeId) {

        Employee employee = (Employee) employeeRepository.findByEmployeeId(employeeId).orElseThrow(() ->

                                new RuntimeException("Employee not found"));

        // ALREADY CHECKED IN

        if (attendanceRepository.existsByEmployeeIdAndDate(employeeId, LocalDate.now())) {

            throw new RuntimeException(
                    "Already checked in today"
            );
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setEmployeeName(employee.getEmployeeName());
        attendance.setDate(LocalDate.now());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("PRESENT");
        attendanceRepository.save(attendance);
        return "Checked In Successfully";
    }

    public String checkOut(String employeeId) {

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now()
                        ).orElseThrow(() ->
                                new RuntimeException("Attendance not found"));

        if (

                attendance.getCheckOutTime()
                        != null
        ) {

            throw new RuntimeException(
                    "Already checked out"
            );
        }

        attendance.setCheckOutTime(LocalDateTime.now());

        // WORKING HOURS

        double hours = Duration.between(
                        attendance.getCheckInTime(),
                        attendance.getCheckOutTime()
                ).toMinutes() / 60.0;
        attendance.setWorkingHours(
                hours
        );
        attendanceRepository.save(attendance);

        return "Checked Out Successfully";
    }

    // TODAY ATTENDANCE

    public Attendance getTodayAttendance(String employeeId) {

        return attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now()
                ).orElse(null);
    }

    // TOTAL ATTENDANCE

    public long getTotalAttendance(String employeeId) {

        return attendanceRepository.countByEmployeeId(employeeId);
    }

    // LEAVE BALANCE

// LEAVE BALANCE

    public long getLeaveBalance(

            String employeeId
    ) {

        // FIND EMPLOYEE

        Employee employee =

                (Employee) employeeRepository
                        .findByEmployeeId(
                                employeeId
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        // TOTAL COMPANY LEAVES

        long totalLeaves = 24;

        // EMPLOYEE JOINING DATE

        LocalDate joiningDate =

                employee.getJoiningDate();

        // TOTAL WORKING DAYS SINCE JOINING

        long workingDays =

                java.time.temporal.ChronoUnit.DAYS
                        .between(

                                joiningDate,

                                LocalDate.now()
                        ) + 1;

        // TOTAL PRESENT DAYS

        long attendanceDays =

                attendanceRepository
                        .countByEmployeeId(
                                employeeId
                        );

        // USED LEAVES

        long usedLeaves =

                workingDays
                        - attendanceDays;

        // SAFETY CHECK

        if (usedLeaves < 0) {

            usedLeaves = 0;
        }

        // REMAINING LEAVES

        long remainingLeaves =

                totalLeaves
                        - usedLeaves;

        // FINAL SAFETY

        return Math.max(
                remainingLeaves,
                0
        );
    }}