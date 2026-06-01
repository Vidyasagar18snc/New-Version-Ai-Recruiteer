package com.Vendor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "panels")
public class Panel {

    @Id
    private String id;

    private String name;

    private String email;

    private String role;

    private boolean available = true;

    private String password;
}