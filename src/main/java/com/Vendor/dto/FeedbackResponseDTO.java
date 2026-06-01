package com.Vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackResponseDTO {

    private String name;
    private String role;
    private int experience;

    private double percentageScore; // ✅ 68.2%
    private int rank;               // ✅ #1, #2, #3

    private String decision;        // HIRE / HOLD / REJECT
    private String summary;
    private String createdBy;// AI Summary
}