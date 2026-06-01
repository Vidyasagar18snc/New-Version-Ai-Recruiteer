package com.Vendor.controller;

import com.Vendor.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@CrossOrigin
public class OnboardingController {

    private final DocumentService documentService;
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
}