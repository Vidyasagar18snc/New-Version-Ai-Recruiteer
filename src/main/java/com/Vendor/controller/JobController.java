package com.Vendor.controller;

import com.Vendor.model.Job;
import com.Vendor.service.DocumentService;
import com.Vendor.service.JobService;
import com.Vendor.util.JDParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final JDParsingService jdParsingService;
    private final DocumentService employeeService;

    @PostMapping("/jobs")
    public ResponseEntity<Job> create(@RequestBody Job job) {

        Job createdJob = jobService.createJob(job);

        return ResponseEntity.ok(createdJob);
    }
        @GetMapping("/latest")
    public ResponseEntity<?> getLatestJob() {
        try {
            return ResponseEntity.ok(jobService.getLatestJob());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No job found");
        }
    }
    @GetMapping("/jobs")
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @PostMapping("/upload-jd")
    public ResponseEntity<Job> uploadJD(
            @RequestParam("file") MultipartFile file) {

        Job job = jdParsingService.processJD(file);
        return ResponseEntity.ok(job);
    }
    @PutMapping("/jobs/{id}")
    public Job updateJob(
            @PathVariable String id,
            @RequestBody Job request
    ) {
        return jobService.updateJob(id, request);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable String id) {

        jobService.deleteById(id);

        return ResponseEntity.ok("Job deleted successfully");
    }
}
