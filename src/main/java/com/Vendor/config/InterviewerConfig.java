package com.Vendor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "interview")
public class InterviewerConfig {

    private List<Interviewer> interviewers;

    @Data
    public static class Interviewer {
        private String id;
        private String name;
        private String email;
    }
}