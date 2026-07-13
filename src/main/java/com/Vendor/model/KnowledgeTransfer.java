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

    // who receives the KT
    private String transferToEmployeeId;
    private String transferToEmployeeName;

    private String documentName;
    private String documentS3Key;
    private String documentUrl;

    private String status; // PENDING | COMPLETED

    private LocalDate ktDate;
}