package com.Vendor.model;

import lombok.Data;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data

@Document(collection = "knowledge_transfer")

public class KnowledgeTransfer {

    @Id
    private String id;

    private String employeeId;

    private String employeeName;

    private String department;

    private String projectName;

    private String taskDetails;

    private String documentationLink;

    private String credentialsShared;

    private String transferredTo;

    private String remarks;

    private String status;

    private LocalDate ktDate;
}