package com.Vendor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("EmployeeDocuments")
public class EmployeeDocument {

    @Id
    private String id;

    private String candidateId;
    private String candidateName;

    private String documentType;

    private String fileName;

    private String s3Key;
    private String fileUrl;

    private String verificationStatus;
    private String verificationRemarks;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
}