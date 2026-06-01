package com.Vendor.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class JobRequest {
    private String title;
    private int experience;
    private List<String> skills;
    private String description;
    private String location;
}