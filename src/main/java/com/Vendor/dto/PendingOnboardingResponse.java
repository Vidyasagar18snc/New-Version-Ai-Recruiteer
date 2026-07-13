package com.Vendor.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingOnboardingResponse {
    private long currentMonthPendingCount;
    private long lastMonthPendingCount;
    private long change;
}

