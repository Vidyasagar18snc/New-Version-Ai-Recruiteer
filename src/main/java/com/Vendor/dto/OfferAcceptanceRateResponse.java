package com.Vendor.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferAcceptanceRateResponse {
    private double currentMonthRate;
    private double lastMonthRate;
    private double change;
}
