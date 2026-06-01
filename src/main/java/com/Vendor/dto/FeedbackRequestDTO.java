package com.Vendor.dto;

import lombok.Data;

@Data
public class FeedbackRequestDTO {

    private String name;
    private String role;
    private int experience;
    private String round;

    private int technical;
    private int communication;
    private int problemSolving;

    private String createdBy;
}