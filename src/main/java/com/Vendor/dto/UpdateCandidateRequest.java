package com.Vendor.dto;

import lombok.Data;

@Data
public class UpdateCandidateRequest {
    private String name;
    private String role;
    private Integer score;
    private String status;   // "Shortlisted" | "Review" | "Rejected"
}