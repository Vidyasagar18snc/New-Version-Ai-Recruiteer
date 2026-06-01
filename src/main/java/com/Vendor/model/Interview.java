package com.Vendor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Data
@Document(collection = "interviews")
public class Interview {

    @Id
    private String id;
    private String candidateId;
    private String candidateName;
    private Date startTime;
    private Date endTime;
    private String meetLink;
    private String interviewerId;
    private String interviewerEmail;
    private String status;
    private Date createdAt = new Date();

}