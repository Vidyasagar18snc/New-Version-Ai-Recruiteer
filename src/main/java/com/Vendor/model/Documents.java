package com.Vendor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data @AllArgsConstructor @NoArgsConstructor
@Document(collection = "documents")
public class Documents {

    @Id
    private String id;

    private String candidateId;
    private String documentType;
    private String status; // VERIFIED / REJECTED / REVIEW

    private String pan;
    private String aadhaar;
    private String accountNumber;
}