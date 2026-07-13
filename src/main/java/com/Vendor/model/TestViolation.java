package com.Vendor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "test_violations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestViolation {

    @Id
    private String id;

    private String candidateId;

    private String token;

    private String violationType;

    private Integer warningCount;

    private LocalDateTime timestamp;
}