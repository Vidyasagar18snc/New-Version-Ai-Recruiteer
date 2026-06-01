package com.Vendor.controller;

import com.Vendor.dto.LoginRequest;
import com.Vendor.dto.ResetPasswordRequest;
import com.Vendor.model.Asset;
import com.Vendor.service.AssetService;
import com.Vendor.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AssetService assetService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                employeeService.login(request));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        employeeService.resetPassword(request.getEmail(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(java.util.Map.of("message", "Password reset successful"));
    }
    @GetMapping("/getAll/{employeeId}")
    public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployee(employeeId));
    }
    @PostMapping("/create")
    public ResponseEntity<?> createAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(assetService.createAsset(asset));
    }
    @GetMapping("/assets")
    public ResponseEntity<?> getAllAssets() {return ResponseEntity.ok(assetService.getAllAssets());
    }
    @PostMapping("/assign")

    public ResponseEntity<?> assignAsset(

            @RequestParam String assetId,
            @RequestParam String employeeId,
            @RequestParam String assignedBy,
            @RequestParam String location,
            @RequestParam String condition,
            @RequestParam String remarks
    ) {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        assetService.assignAsset(
                                assetId,
                                employeeId,
                                assignedBy,
                                location,
                                condition,
                                remarks
                        )
                )
        );
    }
    @GetMapping("/employee/{employeeId}")

    public ResponseEntity<?> getEmployeeAssets(@PathVariable String employeeId) {
        return ResponseEntity.ok(

                assetService.getEmployeeAssets(employeeId));
    }
    @PostMapping("/return")

    public ResponseEntity<?> returnAsset(@RequestParam String assetId,
                                         @RequestParam String employeeId
    ) {

        return ResponseEntity.ok(

                java.util.Map.of("message",
                        assetService.returnAsset(assetId, employeeId))
        );
    }
    @PutMapping("/update/{id}")

    public ResponseEntity<?> updateAsset(


            @PathVariable String id,

            @RequestBody
            Asset asset
    ) {

        return ResponseEntity.ok(

                assetService.updateAsset(

                         id,

                        asset
                )
        );
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAsset(

            @PathVariable String id
    ) {

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

                employeeService
                        .getAllEmployees()
        );
    }
    @GetMapping("/tracking")

    public ResponseEntity<?> getAllAssignments() {

        return ResponseEntity.ok(

                assetService
                        .getAllAssignments()
        );
    }
    @GetMapping("/assigned")

    public ResponseEntity<?> getAssignedAssets() {

        return ResponseEntity.ok(

                assetService
                        .getAssignedAssets()
        );
    }
    @GetMapping("/employee-history/{employeeId}")

    public ResponseEntity<?> getEmployeeAssetHistory(

            @PathVariable
            String employeeId
    ) {

        return ResponseEntity.ok(

                assetService
                        .getEmployeeAssetHistory(
                                employeeId
                        )
        );
    }
    @GetMapping("/history/{assetId}")

    public ResponseEntity<?> getAssetHistory(

            @PathVariable
            String assetId
    ) {

        return ResponseEntity.ok(

                assetService
                        .getAssetHistory(
                                assetId
                        )
        );
    }
    @GetMapping("/employees/count")
    public ResponseEntity<Long> getTotalEmployeesCount() {
        return ResponseEntity.ok(employeeService.getTotalEmployeesCount());
    }
}
