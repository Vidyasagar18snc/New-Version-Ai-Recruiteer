package com.Vendor.util;

import java.time.Year;
import java.util.UUID;

public class EmployeeUtil {

    public static String generateEmployeeId() {
        return "EMP" + Year.now().getValue() +
                UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public static String generateCompanyEmail(String name) {
        String formatted = name.toLowerCase().replaceAll("\\s+", ".");
        return formatted + "@yourcompany.com";
    }
}