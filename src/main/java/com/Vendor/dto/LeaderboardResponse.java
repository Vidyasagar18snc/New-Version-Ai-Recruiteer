package com.Vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardResponse {

    private String candidateName;
    private int score;
    private long timeTaken;
    private int rank;
}