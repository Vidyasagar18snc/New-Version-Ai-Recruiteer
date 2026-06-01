package com.Vendor.dto;

import lombok.Data;

@Data

public class DeboardingRequest {

    private String employeeId;

    private String reason;

    private String lastWorkingDate;

    private String remarks;

    private String initiatedBy;
}