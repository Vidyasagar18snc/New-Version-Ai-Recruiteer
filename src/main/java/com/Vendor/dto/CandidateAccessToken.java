package com.Vendor.dto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "candidate_access_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAccessToken {
    @Id
    private String id;

    private String candidateId;

    private String token;

    private LocalDateTime expiryTime;

    private boolean used;
}