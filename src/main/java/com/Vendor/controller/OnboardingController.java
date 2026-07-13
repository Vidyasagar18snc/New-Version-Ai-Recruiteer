package com.Vendor.controller;

import com.Vendor.dto.PendingOnboardingResponse;
import com.Vendor.model.Onboarding;
import com.Vendor.service.DocumentService;
import com.Vendor.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final DocumentService documentService;
    private final OnboardingService onboardingService;
    @PostMapping("/upload-document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam MultipartFile[] files,
            @RequestParam String candidateId,
            @RequestParam String candidateName
    ) {

        try {
            documentService.uploadDocuments(
                    files,
                    candidateId,
                    candidateName
            );
            return ResponseEntity.ok().body(
                    java.util.Map.of(

                            "message",

                            "Documents uploaded successfully"
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(

                    java.util.Map.of(

                            "message",

                            e.getMessage()
                    )
            );
        }
    }
    @GetMapping("/documents")
    public ResponseEntity<?> getDocuments() {

        return ResponseEntity.ok(

                documentService.getAllDocuments()
        );
    }
    @PostMapping("/verify-all-documents")
    public ResponseEntity<?> verifyAllDocuments(@RequestParam String candidateId) {

        try {

            documentService.verifyAllDocuments(candidateId);

            return ResponseEntity.ok().body(

                    java.util.Map.of("message", "All documents verified successfully"));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(

                    java.util.Map.of("message", e.getMessage())
            );
        }
    }
    @PostMapping("/background-verification")
    public ResponseEntity<?> sendVerification(
            @RequestParam  String id,
            @RequestParam String hrEmail
    ) {

        onboardingService.sendVerification(
                id,
                hrEmail
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Verification request sent successfully"
                )
        );
    }
    @GetMapping("/all")
    public ResponseEntity<List<Onboarding>> getAllOnboardingCandidates() {

        return ResponseEntity.ok(
                onboardingService.getAllOnboardingCandidates()
        );
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getOnboardingCount() {
        return ResponseEntity.ok(
                onboardingService.getOnboardingCount()
        );
    }
    @GetMapping("/pending-summary") public ResponseEntity<PendingOnboardingResponse> getPendingOnboardingSummary() {
        return ResponseEntity.ok(onboardingService.getPendingOnboardingSummary()); }
}