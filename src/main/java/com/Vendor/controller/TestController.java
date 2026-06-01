package com.Vendor.controller;

import com.Vendor.dto.*;
import com.Vendor.model.TestResult;
import com.Vendor.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    //  INTERVIEW LINK GENERATION (FIXED)
    @PostMapping("/generate-interview")
    public ResponseEntity<String> generateInterviewLink(
            @RequestBody GenerateLinkRequestDTO request) {

        String link = testService.generateTestLink(request);
        return ResponseEntity.ok(link);
    }

    // INTERVIEW TOKEN VALIDATION (FIXED)
    @GetMapping("test/validate")
    public ResponseEntity<ValidateResponseDTO> validateInterviewToken(
            @RequestParam String token) {

        ValidateResponseDTO result = testService.validateToken(token);

        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/all")
    public ResponseEntity<List<TestResult>> getAllRanking() {
        return ResponseEntity.ok(testService.getAllRankedCandidates());
    }
    @PostMapping("/submit")
    public ResponseEntity<TestResultResponse> submitTest(
            @RequestBody TestSubmissionRequest request) {

        TestResultResponse response = testService.evaluateTest(request);
        return ResponseEntity.ok(response);
    }



}