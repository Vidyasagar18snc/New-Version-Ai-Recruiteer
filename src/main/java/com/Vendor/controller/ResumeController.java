package com.Vendor.controller;

import com.Vendor.dto.SlotSelectionRequest;
import com.Vendor.dto.UpdateCandidateRequest;
import com.Vendor.model.Candidate;
import com.Vendor.model.CandidateResponse;
import com.Vendor.model.Interview;
import com.Vendor.model.InterviewRequest;
import com.Vendor.repository.CandidateRepository;
import com.Vendor.service.InterviewService;
import com.Vendor.service.ResumeService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final InterviewService interviewService;
    private final CandidateRepository candidateRepository;

    @PostMapping("/upload")
    public CandidateResponse upload(
            @RequestParam MultipartFile file,
            @RequestParam String role) {

        return resumeService.processResume(file, role);
    }

    @GetMapping("/resume")
    public List<CandidateResponse> getAll() {
        return resumeService.getAllCandidates();
    }

    @PostMapping("/schedule-interview")
    public Interview scheduleInterview(
            @RequestBody InterviewRequest request
    ) {

        return interviewService.scheduleInterview(
                request.getCandidateName(),
                request.getCandidateId(),
                request.getPanelEmail(),
                request.getSelectedSlot()
        );
    }
    @PostMapping("/select-slot")
    public ResponseEntity<?> selectSlot(
            @RequestBody SlotSelectionRequest request
    ) {

        interviewService.scheduleInterviewUsingToken(request);

        Map<String, String> response = new HashMap<>();

        response.put("message", "Interview Scheduled Successfully");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/slots")
    public List<String> getSlots(@RequestParam String token) {

        return interviewService.getSlotsByToken(token);
    }
    @PutMapping("/update-dashboard/{id}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable String id,
            @RequestBody CandidateResponse request) {

        return ResponseEntity.ok(resumeService.updateCandidate(id, request));
    }
    @DeleteMapping("/resume/{id}")
    public ResponseEntity<Map<String, String>> deleteCandidate(@PathVariable String id) {

        resumeService.deleteCandidate(id);

        return ResponseEntity.ok(
                Collections.singletonMap("message", "Candidate deleted successfully")
        );

    }
    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable String id,
            @RequestBody UpdateCandidateRequest request) {

        CandidateResponse response = resumeService.updateCandidateStatus(id, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/panel/accept")
    public ResponseEntity<?> acceptPanel(
            @RequestParam String candidateId
    ) {

        interviewService.acceptPanel(
                candidateId
        );

        return ResponseEntity.ok(
                "Interview Confirmed Successfully"
        );
    }
    @GetMapping("/panel/decline")
    public ResponseEntity<?> declinePanel(
            @RequestParam String candidateId
    ) {

        interviewService.declinePanel(
                candidateId
        );

        return ResponseEntity.ok(
                "Panel Reassigned Successfully"
        );
    }
    @GetMapping("/candidates/shortlisted/count")
    public ResponseEntity<Long> getShortlistedCount() {
        return ResponseEntity.ok(resumeService.getShortlistedCount());
    }
    @GetMapping("/candidates/uploaded/count")
    public ResponseEntity<Map<String, Long>> getTotalUploadedResumes() {

        long count = resumeService.getTotalUploadedResumes();

        return ResponseEntity.ok(
                Map.of("totalUploadedResumes", count)
        );
    }
    @GetMapping("/resume/view/{candidateId}")
    public ResponseEntity<Resource> viewResume(@PathVariable String candidateId) throws IOException {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        Path path = Paths.get("uploads/resumes/" + candidate.getResumeUrl()).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume not found");
        }

        String fileName = candidate.getResumeUrl().toLowerCase();

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (fileName.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".doc")) {
            mediaType = MediaType.parseMediaType("application/msword");
        } else if (fileName.endsWith(".docx")) {
            mediaType = MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            );
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}