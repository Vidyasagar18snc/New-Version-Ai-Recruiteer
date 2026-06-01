
package com.Vendor.service;

import com.Vendor.dto.CandidateAccessToken;
import com.Vendor.dto.GenerateLinkRequestDTO;
import com.Vendor.model.*;
import com.Vendor.repository.CandidateAccessTokenRepository;
import com.Vendor.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final CandidateRepository candidateRepository;
    private final JobService jobService;
    private final EmailService emailService;
    private final TestService testService;
    private final InterviewService interviewService;
    private final PanelService panelService;
    private final AvailabilityService availabilityService;
    private final CandidateAccessTokenRepository tokenRepository;
    @Value("${app.hr.email}")
    private String hrEmail;

    public CandidateResponse processResume(MultipartFile file, String roleFromHR) {
        String resumeText =
                extractText(file);

        String email =
                extractEmail(resumeText);

        String name =
                extractName(resumeText);

        double experience =
                extractExperience(resumeText);

        List<String> resumeSkills =
                extractSkills(resumeText);

        Candidate candidate =
                new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        String role;
        int score = 0;
        String status;
        List<String> matchedSkills =
                new ArrayList<>();

        List<String> extraSkills =
                new ArrayList<>();
        Job job =
                jobService.getJobByRole(roleFromHR);

        if (job == null) {
            role = roleFromHR;
            status = "Rejected";
            extraSkills = resumeSkills;

        } else {

            matchedSkills = resumeSkills.stream()
                    .filter(skill ->
                            job.getSkills().stream()
                                    .anyMatch(jd ->
                                            jd.equalsIgnoreCase(skill)
                                    )
                    )
                    .collect(Collectors.toList());
            extraSkills = resumeSkills.stream()
                    .filter(skill ->
                            job.getSkills().stream()
                                    .noneMatch(jd ->
                                            jd.equalsIgnoreCase(skill)
                                    )
                    )
                    .collect(Collectors.toList());

            score = calculateFinalScore(
                    job,
                    resumeText
            );

            status = (score >= 85)
                    ? "Shortlisted"
                    : (score >= 70)
                    ? "Review"
                    : "Rejected";
            role = job.getTitle();
        }
        candidate.setRole(role);
        candidate.setScore(score);
        candidate.setStatus(status);
        candidate.setSkills(resumeSkills);
        candidate.setMatchedSkills(matchedSkills);
        candidate.setExtraSkills(extraSkills);
        candidate.setExperience(experience);


// ================= DUPLICATE CHECK =================

// 1. EMAIL CHECK

        // EMAIL CHECK

        if (email != null &&
                candidateRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Candidate already exists with same email"
            );
        }


// NAME + EXPERIENCE + SKILLS CHECK

        List<Candidate> existingCandidates =
                candidateRepository.findByName(name);

        for (Candidate existing : existingCandidates) {

            boolean sameExperience =
                    existing.getExperience() == experience;

            boolean sameSkills =
                    existing.getSkills() != null &&
                            existing.getSkills().containsAll(resumeSkills);

            if (sameExperience && sameSkills) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate resume detected"
                );
            }
        }


// ================= SAVE CANDIDATE =================

        candidate =
                candidateRepository.save(candidate);
        String panelName = null;
        String panelEmail = null;
        List<String> freeSlots =
                new ArrayList<>();

        try {

            if (email != null) {
                if ("Rejected".equalsIgnoreCase(status)) {
                    emailService.sendStatusEmail(
                            email,
                            name,
                            "NOT_MATCH"
                    );
                }
                else if ("Review".equalsIgnoreCase(status)) {
                    emailService.sendStatusEmail(
                            email,
                            name,
                            "UNDER_REVIEW"
                    );
                }
                else if ("Shortlisted".equalsIgnoreCase(status)) {
                    if (candidate.getExperience() <= 0.5) {
                        GenerateLinkRequestDTO request =
                                new GenerateLinkRequestDTO();

                        request.setCandidateId(
                                candidate.getId()
                        );
                        request.setTestId(
                                "GENERAL_TEST"
                        );
                        String testLink =
                                testService.generateTestLink(
                                        request
                                );
                        candidate.setInterviewLink(
                                testLink
                        );
                        candidateRepository.save(candidate);
                        emailService.sendTestLink(
                                email,
                                testLink
                        );
                        System.out.println(
                                "🧠 Fresher → Test Link Generated"
                        );
                    } else {

                        // Assign Panel new Logics
                        List<Panel> panels =
                                panelService.assignPanel(role);

                        if (panels == null || panels.isEmpty()) {

                            throw new RuntimeException(
                                    "No panel available"
                            );
                        }

                        Panel panel = panels.get(0);

                        panelName = panel.getName();

                        panelEmail = panel.getEmail();
                        candidate.setPanelName(panelName);

                        candidate.setPanelEmail(panelEmail);
                        candidate.setAssignedPanelId(panel.getId());
                        candidateRepository.save(candidate);
                        // Fetch interviewer free slots
                        freeSlots =
                                availabilityService.getFreeSlots(panelEmail, LocalDate.now()
                                                .plusDays(1)
                                                .toString()
                                );

                        System.out.println("Free Slots : " + freeSlots);

                        // Generate Secure Token
                        String accessToken =
                                UUID.randomUUID().toString();

                        // Save Token
                        CandidateAccessToken token =
                                CandidateAccessToken.builder()
                                        .candidateId(candidate.getId())
                                        .token(accessToken)
                                        .expiryTime(LocalDateTime.now().plusHours(24))
                                        .used(false)
                                        .build();

                        tokenRepository.save(token);

                        // Send secure slot mail
                        emailService.sendCandidateSlotSelectionMail(
                                email,
                                candidate.getName(),
                                candidate.getRole(),
                                freeSlots,
                                accessToken
                        );

                        System.out.println("Secure slot mail sent successfully");
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return CandidateResponse.builder()
                .candidateId(candidate.getId())
                .name(name)
                .role(role)
                .score(score)
                .status(status)
                .matchedSkills(matchedSkills)
                .extraSkills(extraSkills)
                .panelName(panelName)
                .panelEmail(panelEmail)
                .freeSlots(freeSlots)
                .build();
    }
    private int calculateFinalScore(Job job, String resumeText) {

        resumeText = resumeText.toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ");

        int matchCount = 0;

        for (String skill : job.getSkills()) {
            String cleanSkill = skill.toLowerCase()
                    .replaceAll("[^a-z0-9 ]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (resumeText.contains(cleanSkill)) {
                matchCount++;
            }
        }

        int totalSkills = job.getSkills().size();

        double candidateExp = extractExperience(resumeText);
        double requiredExp = job.getExperience();

        double skillScore = ((double) matchCount / totalSkills) * 100;

        double expScore;

        // ✅ tolerance logic
        if (candidateExp >= requiredExp * 0.9) {
            expScore = 100;
        } else {
            expScore = (candidateExp / requiredExp) * 100;
        }

        expScore = Math.min(expScore, 100);

        double finalScore = (skillScore * 0.7) + (expScore * 0.3);

        return (int) finalScore;
    }

    // ================= EXPERIENCE =================
    private double extractExperience(String text) {

        Pattern pattern = Pattern.compile(
                "(\\d+(\\.\\d+)?)\\s*(year|years|yr|yrs)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(text);

        double maxExp = 0;

        while (matcher.find()) {
            double exp = Double.parseDouble(matcher.group(1));
            if (exp > maxExp) {
                maxExp = exp;
            }
        }

        return maxExp;
    }
    private List<String> extractSkills(String resumeText) {

        List<String> skills = new ArrayList<>();

        resumeText = resumeText.toLowerCase();

        // ✅ STRICT: stop at double newline (section boundary)
        Pattern pattern = Pattern.compile(
                "(skills|technical skills|technologies|tech stack)\\s*[:\\-]?\\s*(.*?)\\n\\s*\\n",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(resumeText);

        if (matcher.find()) {

            String skillsBlock = matcher.group(2);

            String[] tokens = skillsBlock.split("[,\\n•|]");

            for (String token : tokens) {

                String skill = token.trim();

                // ✅ CLEAN
                skill = skill.replaceAll("[^a-zA-Z0-9+#. ]", "").trim();

                // ✅ STRICT FILTER
                if (isValidSkill(skill)) {
                    skills.add(skill);
                }
            }
        }

        return skills.stream().distinct().collect(Collectors.toList());
    }

    private boolean isValidSkill(String skill) {

        if (skill == null || skill.isEmpty()) return false;


        if (skill.split(" ").length > 3) return false;


        if (skill.contains("@") || skill.contains(".com") || skill.contains("http")) return false;

        List<String> ignore = List.of(
                "summary", "profile", "experience", "education",
                "project", "developer", "engineer", "skills"
        );

        if (ignore.contains(skill)) return false;

        return true;
    }


    // ================= UTIL =================
    private String extractText(MultipartFile file) {
        try {
            Tika tika = new Tika();
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing resume", e);
        }
    }

    private String extractEmail(String text) {

        Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        );

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String email = matcher.group();
            if (!email.toLowerCase().contains("example")) {
                return email;
            }
        }

        return null;
    }

    private String extractName(String text) {

        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()
                    && line.trim().length() < 50
                    && !line.toLowerCase().contains("resume")
                    && !line.contains("@")) {

                return line.trim();
            }
        }

        return "Unknown Candidate";
    }

    public List<CandidateResponse> getAllCandidates() {

        return candidateRepository.findAll()
                .stream()
                .map(c -> CandidateResponse.builder()
                        .name(c.getName())
                        .role(c.getRole())
                        .score(c.getScore())
                        .status(c.getStatus())

                        .skills(
                                c.getSkills() != null ? c.getSkills() : new ArrayList<>()
                        )

                        .matchedSkills(
                                c.getMatchedSkills() != null ? c.getMatchedSkills() : new ArrayList<>()
                        )
                        .extraSkills(
                                c.getExtraSkills() != null ? c.getExtraSkills() : new ArrayList<>()
                        )
                        .build())
                .collect(Collectors.toList());
    }

}