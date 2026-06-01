package com.Vendor.model;

import lombok.Data;

@Data
public class InterviewRequest {

    private String candidateId;

    private String candidateName;

    private String panelEmail;

    private String selectedSlot;

    private String role;
}