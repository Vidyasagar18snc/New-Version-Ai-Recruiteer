package com.Vendor.dto;

import lombok.Data;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data

@Document(collection = "asset_assignments")

public class AssetAssignment {

    @Id
    private String id;

    private String employeeId;

    private String employeeName;
    private String assetId;
    private String assetName;
    private String brand;
    private String serialNumber;
    private LocalDate assignedDate;
    private LocalDate returnedDate;
    private String status;
    private String category;
    private String assignedBy;

    private String returnedBy;

    private String condition;

    private String remarks;

    private String location;

}