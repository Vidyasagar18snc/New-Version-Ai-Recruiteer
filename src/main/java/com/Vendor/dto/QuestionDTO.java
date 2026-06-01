package com.Vendor.dto;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {

    private String id;
    private String question;
    private List<String> options;
}