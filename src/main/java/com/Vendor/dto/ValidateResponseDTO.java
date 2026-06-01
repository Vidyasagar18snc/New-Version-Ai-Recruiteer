package com.Vendor.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateResponseDTO {

    private boolean valid;
    private String message;

    private String candidateId;
    private String testId;
    private int duration;
}