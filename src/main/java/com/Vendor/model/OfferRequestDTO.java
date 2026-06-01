package com.Vendor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "offers")
public class OfferRequestDTO {

    @Id
    private String candidateId;

    private String name;

    private String email;

    private String role;

    private double salary;

    private String joiningDate;

    private String companyName;

    private String companyAddress;

    private String address;

    private String location;

    private String department;

    private String employmentType;

    private String hrEmail;

    private String hrPhone;

    private String hrSignatoryName;

    private String hrSignatoryTitle;

    // NEW FIELDS

    private String offerPdfUrl;

    private String offerToken;

    private String offerStatus;

    private String rejectionReason;
    private String rejectionComment;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;
}