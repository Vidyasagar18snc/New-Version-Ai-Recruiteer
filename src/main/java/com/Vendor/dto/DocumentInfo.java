package com.Vendor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentInfo {

    private String documentType;
    private String candidateId;

    private String candidateName;
    private String fileName;

    private String s3Key;
    private String fileUrl;
    private String verificationStatus;

    private String verificationRemarks;

    private LocalDateTime uploadedAt;

    private LocalDateTime verifiedAt;
}