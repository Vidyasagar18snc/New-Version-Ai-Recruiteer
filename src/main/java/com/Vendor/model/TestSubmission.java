package com.Vendor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
class TestSubmission {
    private  String candidateId;
    private   String testId;
    private int score;
    private int total;
    private long duration;
    private boolean evaluated;
    private int rank;
}