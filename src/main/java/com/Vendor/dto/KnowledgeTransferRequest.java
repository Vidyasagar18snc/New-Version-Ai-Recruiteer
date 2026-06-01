package com.Vendor.dto;

import lombok.Data;

@Data

public class KnowledgeTransferRequest {

    private String employeeId;

    private String projectName;

    private String taskDetails;

    private String documentationLink;

    private String credentialsShared;

    private String transferredTo;

    private String remarks;
}