package com.Vendor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TestResultResponse {

    private int score;
    private int total;
    private double percentage;
    private String status; // PASS / FAIL
    private int rank;

}