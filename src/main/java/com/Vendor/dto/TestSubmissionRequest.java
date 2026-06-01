package com.Vendor.dto;


import lombok.Data;
import java.util.List;

@Data
public class TestSubmissionRequest {

    private String token;

    private List<AnswerDTO> answers;
}