package com.Vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViolationResponse {

    private int warningCount;

    private boolean blocked;

    private String message;
}