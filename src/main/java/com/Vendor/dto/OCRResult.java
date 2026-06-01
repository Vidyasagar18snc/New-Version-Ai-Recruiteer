package com.Vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class OCRResult {

    private String documentType;
    private String name;
    private String pan;
    private String aadhaar;
    private String accountNumber;
    private String status; // VERIFIED / REJECTED / REVIEW
    private String rawText;

    // getters & setters
}