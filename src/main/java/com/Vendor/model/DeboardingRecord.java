package com.Vendor.model;

import lombok.Data;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data

@Document(collection = "deboarding_records")

public class DeboardingRecord {

    @Id
    private String id;

    private String employeeId;

    private String employeeName;

    private String department;

    private String reason;

    private LocalDate lastWorkingDate;

    private String remarks;

    private String initiatedBy;

    private LocalDate initiatedDate;

    private String status;
}