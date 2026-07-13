package com.Vendor.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    private String id;

    private String name;


    private double experience;
    private List<String> skills;
    private List<String> matchedSkills;
    private List<String> extraSkills;
    private int score;
    private String status;
    private String role;
    private String email;
    private String interviewLink;
    private String offerToken;

    private boolean documentsVerified;

    private String employeeId;
    private String companyEmail;
    private String panelName;
    private String panelEmail;
    private List<String> freeSlots;
    private String assignedPanelId;
    private String selectedSlot;
    private LocalDateTime appliedDate;
    private String panelApprovalStatus;
    private String resumeUrl;


}