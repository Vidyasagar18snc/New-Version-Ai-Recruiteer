package com.Vendor.controller;

import com.Vendor.dto.SlotSelectionRequest;
import com.Vendor.model.CandidateResponse;
import com.Vendor.model.Interview;
import com.Vendor.model.InterviewRequest;
import com.Vendor.service.InterviewService;
import com.Vendor.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final InterviewService interviewService;

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
    public List<String> getSlots(
            @RequestParam String token
    ) {

        return interviewService.getSlotsByToken(
                token
        );
    }

}