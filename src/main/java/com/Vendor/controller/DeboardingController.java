package com.Vendor.controller;

import com.Vendor.dto.AttritionRateResponse;
import com.Vendor.dto.DeboardingRequest;
import com.Vendor.model.DeboardingRecord;
import com.Vendor.model.KnowledgeTransfer;
import com.Vendor.service.DeboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DeboardingController {

    private final DeboardingService deboardingService;



    @PostMapping("/ktinitiate")
    public ResponseEntity<Map<String, String>> initiateKT(
            @RequestParam String employeeId,
            @RequestParam String transferToEmployeeId,
            @RequestParam MultipartFile file
    ) throws Exception {

        String message = deboardingService.initiateKT(
                employeeId,
                transferToEmployeeId,
                file
        );
        return ResponseEntity.ok(Map.of("message", message));
    }


    @PostMapping("/ktcomplete")
    public ResponseEntity<Map<String, String>> completeKT(
            @RequestParam String employeeId
    ) {
        String message = deboardingService.completeKT(employeeId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/kt/all")
    public ResponseEntity<List<KnowledgeTransfer>> getAllKnowledgeTransfers() {
        return ResponseEntity.ok(deboardingService.getAllKnowledgeTransfers());
    }


    @PostMapping("/Deboardinitiate")
    public ResponseEntity<Map<String, String>> initiateDeboarding(
            @RequestBody DeboardingRequest request
    ) {
        String message = deboardingService.initiateDeboarding(request);
        return ResponseEntity.ok(Map.of("message", message));
    }


    @GetMapping("/Deboardingall")
    public ResponseEntity<List<DeboardingRecord>> getAllDeboarding() {
        return ResponseEntity.ok(deboardingService.getAllDeboarding());
    }


    @GetMapping("/completed/count")
    public ResponseEntity<Long> getCompletedDeboardingCount() {
        return ResponseEntity.ok(deboardingService.getCompletedDeboardingCount());
    }
    @GetMapping("/kt/count")
    public ResponseEntity<Long> getTotalKTCount() {
        return ResponseEntity.ok(deboardingService.getTotalKTCount());
    }
    @GetMapping("/deboarding/count")
    public ResponseEntity<Long> getTotalDeboardingCount() {
        return ResponseEntity.ok(deboardingService.getTotalDeboardingCount());
    }
    @GetMapping("/alumni/count")
    public ResponseEntity<Map<String, Long>> getTotalAlumniCount() {

        long count = deboardingService.getTotalAlumniCount();

        return ResponseEntity.ok(
                Map.of("alumniCount", count)
        );
    }
    @GetMapping("/attrition-rate")
    public ResponseEntity<Double> getAttritionRate() {
        return ResponseEntity.ok(deboardingService.getAttritionRate()); }
    @GetMapping("/attrition-rate/last-month")
    public ResponseEntity<Double> getLastMonthAttritionRate() {
        return ResponseEntity.ok(deboardingService.getLastMonthAttritionRate()); }
    @GetMapping("/attrition-rate/change")
    public ResponseEntity<Double> getAttritionRateChange() {
        return ResponseEntity.ok(deboardingService.getAttritionRateChange()); }
    @GetMapping("/attrition-summary")
    public ResponseEntity<AttritionRateResponse> getAttritionSummary() {
        return ResponseEntity.ok(deboardingService.getAttritionSummary()); }
}