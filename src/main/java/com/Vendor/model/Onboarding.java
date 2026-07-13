package com.Vendor.model;

import com.Vendor.dto.DocumentInfo;
import lombok.Data;
import org.apache.poi.hslf.record.CString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("Onboarding")
public class Onboarding {

    @Id
    private String id;

    private String candidateId;
    private String employmentType;
    private String candidateName;

    private String email;

    private String role;

    private String department;

    private String joiningDate;
    private String hrEmail;
    private String onboardingStatus;

    private boolean documentsSubmitted;

    private boolean hrVerified;

    private boolean onboardingCompleted;

    private LocalDateTime createdAt;
    private List<DocumentInfo> documents;
    private String previousCompanyHrEmail;
    private String backgroundVerificationStatus;
    private LocalDateTime verificationMailSentAt;
    private String verificationRemarks;
    private CString Experience;
}