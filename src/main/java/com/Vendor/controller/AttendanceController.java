package com.Vendor.controller;

import com.Vendor.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService
            attendanceService;

    // CHECK IN

    @PostMapping("/checkin/{employeeId}")
    public ResponseEntity<?> checkIn(@PathVariable String employeeId) {

        return ResponseEntity.ok(java.util.Map.of("message",
                        attendanceService
                                .checkIn(employeeId)));
    }

    @PostMapping("/checkout/{employeeId}")
    public ResponseEntity<?> checkOut(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                java.util.Map.of("message",
                        attendanceService.checkOut(employeeId))
        );
    }
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<?> getTodayAttendance(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                attendanceService.getTodayAttendance(employeeId));

    }
    @GetMapping("/total/{employeeId}")
    public ResponseEntity<?> getTotalAttendance(

            @PathVariable
            String employeeId
    ) {

        long totalAttendance =

                attendanceService
                        .getTotalAttendance(
                                employeeId
                        );

        return ResponseEntity.ok(

                java.util.Map.of(

                        "attendanceDays",

                        totalAttendance
                )
        );
    }
    @GetMapping("/leaves/{employeeId}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "leaveBalance",
                        attendanceService.getLeaveBalance(
                                        employeeId)
                )
        );
    }
}
