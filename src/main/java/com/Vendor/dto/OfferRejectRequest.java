package com.Vendor.dto;

import lombok.Data;

@Data
public class OfferRejectRequest {

    private String response;

    private String rejectionReason;

    private String rejectionComment;
}