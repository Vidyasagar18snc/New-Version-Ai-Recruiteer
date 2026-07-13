package com.Vendor.controller;

import com.Vendor.dto.*;
import com.Vendor.model.Asset;
import com.Vendor.service.AssetService;
import com.Vendor.service.EmployeeService;
import com.Vendor.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AssetService assetService;
    private final S3Service s3Service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(employeeService.login(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        employeeService.resetPassword(
                request.getEmail(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful")
        );
    }

    @GetMapping("/getAll/{employeeId}")
    public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                employeeService.getEmployee(employeeId)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(
                assetService.createAsset(asset)
        );
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAllAssets() {
        return ResponseEntity.ok(
                assetService.getAllAssets()
        );
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignAsset(
            @RequestParam String assetId,
            @RequestParam String employeeId,
            @RequestParam String assignedBy,
            @RequestParam String assetType,
            @RequestParam String condition,
            @RequestParam String remarks,
            @RequestParam(required = false) String accessories) {

        return ResponseEntity.ok(
                assetService.assignAsset(
                        assetId,
                        employeeId,
                        assignedBy,
                        assetType,
                        condition,
                        remarks,
                        accessories
                )
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeAssets(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                assetService.getEmployeeAssets(employeeId)
        );
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnAsset(
            @RequestParam String assetId,
            @RequestParam String employeeId) {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        assetService.returnAsset(assetId, employeeId)
                )
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAsset(
            @PathVariable String id,
            @RequestBody Asset asset) {

        return ResponseEntity.ok(
                assetService.updateAsset(id, asset)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable String id) {
        return ResponseEntity.ok(
                Map.of(
                        "message",
                        assetService.deleteAsset(id)
                )
        );
    }

    @GetMapping("/Employee")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(
                employeeService.getAllEmployees()
        );
    }

    @GetMapping("/tracking")
    public ResponseEntity<?> getAllAssignments() {
        return ResponseEntity.ok(
                assetService.getAllAssignments()
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<?> getAssignedAssets() {
        return ResponseEntity.ok(
                assetService.getAssignedAssets()
        );
    }

    @GetMapping("/employee-history/{employeeId}")
    public ResponseEntity<?> getEmployeeAssetHistory(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                assetService.getEmployeeAssetHistory(employeeId)
        );
    }

    @GetMapping("/history/{assetId}")
    public ResponseEntity<?> getAssetHistory(
            @PathVariable String assetId) {

        return ResponseEntity.ok(
                assetService.getAssetHistory(assetId)
        );
    }

    @GetMapping("/employees/count")
    public ResponseEntity<Long> getTotalEmployeesCount() {
        return ResponseEntity.ok(
                employeeService.getTotalEmployeesCount()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestParam String email) {

        return ResponseEntity.ok(
                employeeService.forgotPassword(email)
        );
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> forgotPasswordReset(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {

        return ResponseEntity.ok(employeeService.forgotPasswordReset(
                        email,
                        otp,
                        newPassword));
    }
    @GetMapping("/onboarding/document/view")
    public ResponseEntity<String> viewDocument(
            @RequestParam String s3Key) {

        String url = s3Service.generatePresignedUrl(s3Key);

        return ResponseEntity.ok(url);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        try {
            String message = employeeService.register(request);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/retention-summary")
    public ResponseEntity<RetentionRateResponse> getRetentionSummary() {
        return ResponseEntity.ok(employeeService.getRetentionSummary());
    }
    @GetMapping("/department-overview")
    public ResponseEntity<DepartmentOverviewResponse> getDepartmentOverview() {
        return ResponseEntity.ok(employeeService.getDepartmentOverview());
    }

}