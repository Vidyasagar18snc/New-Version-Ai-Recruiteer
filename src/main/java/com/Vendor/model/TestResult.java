package com.Vendor.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "test_results")
public class TestResult {

    @Id
    private String id;

    private String candidateId;
    private String candidateName;
    private String testId;

    private int score;
    private double percentage;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private long timeTaken;

    private int rank;
}