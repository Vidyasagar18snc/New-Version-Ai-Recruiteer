package com.Vendor.controller;


import com.Vendor.dto.FeedbackRequestDTO;

import com.Vendor.model.Feedback;
import com.Vendor.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public Feedback submitFeedback(@RequestBody FeedbackRequestDTO dto) {
        return service.saveFeedback(dto);
    }

    @GetMapping("/dashboard")
    public List<Feedback> getAllFeedback() {
        return service.getAllFeedback();
    }
}