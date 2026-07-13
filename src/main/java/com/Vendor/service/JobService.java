package com.Vendor.service;


import com.Vendor.model.Employee;
import com.Vendor.model.Job;
import com.Vendor.model.JobRequest;
import com.Vendor.repository.EmployeeRepository;
import com.Vendor.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    public Job createJob(Job request) {

        Job job = Job.builder()
                .title(request.getTitle())
                .experience(request.getExperience())
                .description(request.getDescription())
                .skills(request.getSkills())
                .location(request.getLocation())
                .budget(request.getBudget())
                .build();

        Job savedJob = jobRepository.save(job);

        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {

            if (employee.getPersonalEmail() == null ||
                    employee.getPersonalEmail().isBlank()) {
                continue;
            }

            emailService.sendJobNotification(employee, savedJob);
        }

        return savedJob;
    }    public Job getLatestJob() {
        return jobRepository.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new RuntimeException("No job found"));
    }
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    public Job getJobByRole(String role) {

        List<Job> jobs = jobRepository.findByTitleIgnoreCase(role);

        if (jobs.isEmpty()) {
            return null;
        }

        // ✅ Pick best job (example: latest or highest experience)
        return jobs.get(0);
    }
    public Job updateJob(String jobId, Job request) {

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        // Update all fields
        existingJob.setTitle(request.getTitle());
        existingJob.setExperience(request.getExperience());
        existingJob.setDescription(request.getDescription());
        existingJob.setSkills(request.getSkills());
        existingJob.setLocation(request.getLocation());

        return jobRepository.save(existingJob);
    }

    public void deleteById(String id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found with id: " + id));

        jobRepository.delete(job);
    }
    public long getOpenPositionsCount() {
        return jobRepository.count();
    }
}