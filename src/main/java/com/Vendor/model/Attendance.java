package com.Vendor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "attendance")
public class Attendance {

    @Id
    private String id;
    private String employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String status;

    private double workingHours;
}