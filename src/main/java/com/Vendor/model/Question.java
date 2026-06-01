package com.Vendor.model;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questions")
public class Question {

    @Id
    private String id;

    private String question;
    private List<String> options;

    private String correctAnswer; // ⚠️ NEVER SEND TO FRONTEND
}