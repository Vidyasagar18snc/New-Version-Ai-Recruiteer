package com.Vendor.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection = "employees")

public class Employee {

    @Id
    private String id;

    private String employeeId;

    private String employeeName;

    private String personalEmail;

    private String officialEmail;

    private String department;

    private String role;

    private LocalDate joiningDate;

    private String status;

    private String password;
    private Boolean firstLogin = true;

}