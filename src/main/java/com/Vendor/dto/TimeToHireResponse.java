package com.Vendor.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeToHireResponse {
    private long currentMonthDays;
    private long lastMonthDays;
    private long change;
}

