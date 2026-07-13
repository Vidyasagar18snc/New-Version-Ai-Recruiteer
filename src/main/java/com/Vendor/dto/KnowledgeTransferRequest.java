package com.Vendor.dto;

import lombok.Data;

@Data
public class KnowledgeTransferRequest {

    private String employeeId;

    // who receives the KT
    private String transferToEmployeeId;
    private String transferToEmployeeName;
}