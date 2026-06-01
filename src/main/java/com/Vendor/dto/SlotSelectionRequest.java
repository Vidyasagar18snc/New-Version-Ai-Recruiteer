package com.Vendor.dto;

import lombok.Data;

@Data
public class SlotSelectionRequest {

    private String candidateId;
    private String candidateName;
    private String panelEmail;
    private String selectedSlot;
    private String token;
}