package com.Vendor.dto;

import lombok.Data;

@Data
public class ViolationRequest {

    private String token;

    private String violationType;

}