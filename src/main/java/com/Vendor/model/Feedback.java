package com.Vendor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedback")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    private String id;

    private String name;
    private String role;
    private int experience;
    private String round;

    private int technical;
    private int communication;
    private int problemSolving;

    private double score;
    private String decision;

    private String strengths;
    private String weaknesses;
    private String summary;

    private String createdBy;
    private LocalDateTime createdAt;
}