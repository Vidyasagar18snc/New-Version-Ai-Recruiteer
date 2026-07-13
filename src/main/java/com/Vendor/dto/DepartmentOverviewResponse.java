package com.Vendor.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartmentOverviewResponse {

    private long totalEmployees;

    private long engineering;

    private long Admin;

    private long hr;

    private long operations;

    private long finance;
}