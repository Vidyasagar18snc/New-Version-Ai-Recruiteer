package com.Vendor.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class GenerateLinkRequestDTO {
    private String candidateId;
    private String testId;
    private String candidateEmail;


}