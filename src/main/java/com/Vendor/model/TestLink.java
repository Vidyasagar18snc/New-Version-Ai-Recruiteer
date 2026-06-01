package com.Vendor.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "test_links")
public class TestLink {

    @Id
    private String id;

    private String candidateId;

    // ✅ FIX: field name should be camelCase
    private String candidateEmail;
    private String testId;

    private String token;

    private LocalDateTime expiryTime;

    private boolean attempted;

    // ⏱ TIME TRACKING
    private Long startTime;
    private Long endTime;
    private Long duration;

    // 🧠 ADD THIS (IMPORTANT FOR RANKING)
    private Integer score;
}