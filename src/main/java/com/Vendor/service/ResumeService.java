package com.Vendor.service;

import com.Vendor.dto.CandidateAccessToken;
import com.Vendor.dto.GenerateLinkRequestDTO;
import com.Vendor.dto.UpdateCandidateRequest;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private static final DateTimeFormatter APPLIED_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy HH:mm", Locale.ENGLISH);    private final CandidateRepository candidateRepository;
    private final JobService jobService;
    private final EmailService emailService;
    private final TestService testService;
    private final InterviewService interviewService;
    private final PanelService panelService;
    private final AvailabilityService availabilityService;
    private final CandidateAccessTokenRepository tokenRepository;
    @Value("${app.hr.email}")
    private String hrEmail;
    @Value("${app.fresher.max-experience:0.5}")
    private double fresherMaxExperience;

    public CandidateResponse processResume(MultipartFile file, String roleFromHR) {
        String savedResumeFileName = saveResumeFile(file);
        String resumeText = extractText(file);
        String email = extractEmail(resumeText);
        String name = extractName(resumeText);
        double experience = extractExperience(resumeText);
        List<String> resumeSkills = extractSkills(resumeText);

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setResumeUrl(savedResumeFileName);

        String role;
        int score = 0;
        String status;

        List<String> matchedSkills = new ArrayList<>();
        List<String> extraSkills = new ArrayList<>();

        Job job = jobService.getJobByRole(roleFromHR);

        if (job == null) {

            role = roleFromHR;
            status = "Rejected";
            extraSkills = resumeSkills;

        } else {

            matchedSkills = resumeSkills.stream()
                    .filter(skill ->
                            job.getSkills().stream()
                                    .anyMatch(jd -> jd.equalsIgnoreCase(skill)))
                    .collect(Collectors.toList());

            extraSkills = resumeSkills.stream()
                    .filter(skill ->
                            job.getSkills().stream()
                                    .noneMatch(jd -> jd.equalsIgnoreCase(skill)))
                    .collect(Collectors.toList());

            score = calculateFinalScore(job, resumeText);

            boolean isFresher = experience <= fresherMaxExperience;

            // UPDATED RULE
            if (isFresher) {
                status = score >= 60 ? "Shortlisted" : "Rejected";
            } else {
                status = score >= 80
                        ? "Shortlisted"
                        : score >= 60
                          ? "Review"
                          : "Rejected";
            }

            role = job.getTitle();
        }

        candidate.setRole(role);
        candidate.setScore(score);
        candidate.setStatus(status);
        candidate.setSkills(resumeSkills);
        candidate.setMatchedSkills(matchedSkills);
        candidate.setExtraSkills(extraSkills);
        candidate.setExperience(experience);
        candidate.setAppliedDate(LocalDateTime.now());

        // Email duplicate check
        if (email != null && candidateRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Candidate already exists with same email"
            );
        }

        // Duplicate resume check
        List<Candidate> existingCandidates = candidateRepository.findByName(name);

        for (Candidate existing : existingCandidates) {

            boolean sameExperience = existing.getExperience() == experience;

            boolean sameSkills = existing.getSkills() != null
                    && existing.getSkills().containsAll(resumeSkills);

            if (sameExperience && sameSkills) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate resume detected"
                );
            }
        }

        candidate = candidateRepository.save(candidate);

        String panelName = null;
        String panelEmail = null;
        List<String> freeSlots = new ArrayList<>();

        try {

            if (email != null) {

                if ("Rejected".equalsIgnoreCase(status)) {

                    emailService.sendStatusEmail(email, name, "NOT_MATCH");
                    System.out.println("📧 Rejection email sent to: " + email);

                } else if ("Review".equalsIgnoreCase(status)) {

                    emailService.sendStatusEmail(email, name, "UNDER_REVIEW");
                    System.out.println("📧 Under-review email sent to: " + email);

                } else if ("Shortlisted".equalsIgnoreCase(status)) {

                    boolean isFresher = candidate.getExperience() <= fresherMaxExperience;

                    if (isFresher) {
                        // Fresher → send test link
                        GenerateLinkRequestDTO request = new GenerateLinkRequestDTO();
                        request.setCandidateId(candidate.getId());
                        request.setTestId("GENERAL_TEST");

                        String testLink = testService.generateTestLink(request);

                        candidate.setInterviewLink(testLink);
                        candidateRepository.save(candidate);

                        emailService.sendTestLink(email, testLink);

                        System.out.println("🧠 Fresher shortlisted → Test Link Generated for: " + email);

                    } else {
                        // Experienced → assign panel and send slot selection mail
                        List<Panel> panels = panelService.assignPanel(role);

                        if (panels == null || panels.isEmpty()) {
                            throw new RuntimeException("No panel available");
                        }

                        Panel panel = panels.get(0);

                        panelName = panel.getName();
                        panelEmail = panel.getEmail();
                        candidate.setPanelName(panelName);
                        candidate.setPanelEmail(panelEmail);
                        candidate.setAssignedPanelId(panel.getId());

                        candidateRepository.save(candidate);

                        freeSlots = availabilityService.getFreeSlots(
                                panelEmail,
                                LocalDate.now().plusDays(1).toString()
                        );

                        System.out.println("Free Slots : " + freeSlots);

                        String accessToken = UUID.randomUUID().toString();
                        CandidateAccessToken token = CandidateAccessToken.builder()
                                .candidateId(candidate.getId())
                                .token(accessToken)
                                .expiryTime(LocalDateTime.now().plusHours(24))
                                .used(false)
                                .build();

                        tokenRepository.save(token);

                        emailService.sendCandidateSlotSelectionMail(
                                email,
                                candidate.getName(),
                                candidate.getRole(),
                                freeSlots,
                                accessToken
                        );

                        System.out.println("📅 Experienced shortlisted → Secure slot mail sent to: " + email);
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
                .appliedDate(
                        candidate.getAppliedDate() != null
                                ? candidate.getAppliedDate().format(APPLIED_DATE_FORMAT)
                                : null
                )
                .build();
    }

    private String saveResumeFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/resumes/";
            Files.createDirectories(Paths.get(uploadDir));

            String originalFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFileName;

            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName; // or return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save resume file", e);
        }
    }

    private int calculateFinalScore(Job job, String resumeText) {

        String normalizedResume = resumeText.toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ");

        int matchCount = 0;

        for (String skill : job.getSkills()) {

            String normalizedSkill = skill.toLowerCase()
                    .replaceAll("[^a-z0-9 ]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (normalizedResume.contains(normalizedSkill)) {
                matchCount++;
            }
        }

        double skillScore =
                ((double) matchCount / job.getSkills().size()) * 100;

        double candidateExp = extractExperience(normalizedResume);
        double requiredExp = job.getExperience();

        double expScore = requiredExp <= 0
                ? 100
                : candidateExp >= requiredExp * 0.9
                  ? 100
                  : (candidateExp / requiredExp) * 100;

        expScore = Math.min(expScore, 100);

        return (int) ((skillScore * 0.7) + (expScore * 0.3));
    }

    private double extractExperience(String text) {

        Matcher matcher = Pattern.compile(
                "(\\d+(\\.\\d+)?)\\s*(year|years|yr|yrs)",
                Pattern.CASE_INSENSITIVE
        ).matcher(text);

        double maxExp = 0;

        while (matcher.find()) {
            maxExp = Math.max(
                    maxExp,
                    Double.parseDouble(matcher.group(1))
            );
        }

        return maxExp;
    }
    private List<String> extractSkills(String resumeText) {

        Pattern pattern = Pattern.compile(
                "(skills|technical skills|technologies|tech stack)\\s*[:\\-]?\\s*(.*?)\\n\\s*\\n",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(resumeText.toLowerCase());

        if (!matcher.find()) {
            return Collections.emptyList();
        }

        String block = matcher.group(2);

        return Arrays.stream(block.split("\\n"))
                // strip a leading "label:" prefix per line, e.g. "backend:", "databases & tools:"
                .map(line -> line.replaceFirst("^[a-z0-9 &]+:\\s*", ""))
                .flatMap(line -> Arrays.stream(line.split("[,•|]")))
                .map(String::trim)
                .map(skill -> skill.replaceAll("[^a-zA-Z0-9+#. ]", "").trim())
                .filter(this::isValidSkill)
                .distinct()
                .collect(Collectors.toList());
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
                        .candidateId(c.getId())
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
                        .appliedDate(
                                c.getAppliedDate() != null
                                        ? c.getAppliedDate().format(APPLIED_DATE_FORMAT)
                                        : null
                        )
                        .resumeUrl(c.getResumeUrl())
                        .build())

                .collect(Collectors.toList());
    }

    public CandidateResponse updateCandidate(String id, CandidateResponse request) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found with id: " + id));

        candidate.setName(request.getName());
        candidate.setRole(request.getRole());
        candidate.setScore(request.getScore());
        candidate.setStatus(request.getStatus());

        candidate.setSkills(
                request.getSkills() != null ? request.getSkills() : new ArrayList<>()
        );

        candidate.setMatchedSkills(
                request.getMatchedSkills() != null ? request.getMatchedSkills() : new ArrayList<>()
        );

        candidate.setExtraSkills(
                request.getExtraSkills() != null ? request.getExtraSkills() : new ArrayList<>()
        );

        candidate.setPanelName(request.getPanelName());
        candidate.setPanelEmail(request.getPanelEmail());

        candidate.setFreeSlots(
                request.getFreeSlots() != null ? request.getFreeSlots() : new ArrayList<>()
        );

        Candidate updated = candidateRepository.save(candidate);

        return CandidateResponse.builder()
                .candidateId(updated.getId()) // Mongo ID
                .name(updated.getName())
                .role(updated.getRole())
                .score(updated.getScore())
                .status(updated.getStatus())
                .skills(updated.getSkills() != null ? updated.getSkills() : new ArrayList<>())
                .matchedSkills(updated.getMatchedSkills() != null ? updated.getMatchedSkills() : new ArrayList<>())
                .extraSkills(updated.getExtraSkills() != null ? updated.getExtraSkills() : new ArrayList<>())
                .panelName(updated.getPanelName())
                .panelEmail(updated.getPanelEmail())
                .freeSlots(updated.getFreeSlots() != null ? updated.getFreeSlots() : new ArrayList<>())
                .build();
    }

    public void deleteCandidate(String id) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found with id: " + id));

        candidateRepository.delete(candidate);
    }

    public CandidateResponse updateCandidateStatus(String candidateId, UpdateCandidateRequest request) {


        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Candidate not found"));

        String previousStatus = candidate.getStatus();
        String newStatus      = request.getStatus();

        // ── 2. Apply field updates ───────────────────────────────────────────
        if (request.getName()  != null) candidate.setName(request.getName());
        if (request.getRole()  != null) candidate.setRole(request.getRole());
        if (request.getScore() != null) candidate.setScore(request.getScore());
        if (request.getStatus()!= null) candidate.setStatus(newStatus);

        candidate = candidateRepository.save(candidate);

        boolean statusChanged = newStatus != null && !newStatus.equalsIgnoreCase(previousStatus);

        if (statusChanged && candidate.getEmail() != null) {
            try {
                triggerStatusEmail(candidate, newStatus);
            } catch (Exception e) {
                // Log but don't fail the update
                e.printStackTrace();
            }
        }
        return CandidateResponse.builder()
                .candidateId(candidate.getId())
                .name(candidate.getName())
                .role(candidate.getRole())
                .score(candidate.getScore())
                .status(candidate.getStatus())
                .matchedSkills(candidate.getMatchedSkills())
                .extraSkills(candidate.getExtraSkills())
                .panelName(candidate.getPanelName())
                .panelEmail(candidate.getPanelEmail())
                .build();
    }

    // ── Email trigger — mirrors processResume logic exactly ──────────────────────
    private void triggerStatusEmail(Candidate candidate, String status) throws Exception {

        String email = candidate.getEmail();
        String name  = candidate.getName();
        String role  = candidate.getRole();

        switch (status) {

            case "Rejected" -> {
                emailService.sendStatusEmail(email, name, "NOT_MATCH");
                System.out.println("📧 Rejection email sent to: " + email);
            }
            case "Review" -> {
                emailService.sendStatusEmail(email, name, "UNDER_REVIEW");
                System.out.println("📧 Under-review email sent to: " + email);
            }
            case "Shortlisted" -> {

                if (candidate.getExperience() <= fresherMaxExperience) {
                    GenerateLinkRequestDTO request = new GenerateLinkRequestDTO();
                    request.setCandidateId(candidate.getId());
                    request.setTestId("GENERAL_TEST");
                    String testLink = testService.generateTestLink(request);
                    candidate.setInterviewLink(testLink);
                    candidateRepository.save(candidate);
                    emailService.sendTestLink(email, testLink);
                    System.out.println("🧠 Fresher → Test link re-sent to: " + email);

                } else {
                    List<Panel> panels = panelService.assignPanel(role);

                    if (panels == null || panels.isEmpty()) {
                        throw new RuntimeException("No panel available for role: " + role);
                    }

                    Panel panel = panels.get(0);

                    candidate.setPanelName(panel.getName());
                    candidate.setPanelEmail(panel.getEmail());
                    candidate.setAssignedPanelId(panel.getId());
                    candidateRepository.save(candidate);

                    List<String> freeSlots = availabilityService.getFreeSlots(
                            panel.getEmail(),
                            LocalDate.now().plusDays(1).toString()
                    );

                    String accessToken = UUID.randomUUID().toString();

                    CandidateAccessToken token = CandidateAccessToken.builder()
                            .candidateId(candidate.getId())
                            .token(accessToken)
                            .expiryTime(LocalDateTime.now().plusHours(24))
                            .used(false)
                            .build();
                    tokenRepository.save(token);

                    emailService.sendCandidateSlotSelectionMail(
                            email, name, role, freeSlots, accessToken);

                    System.out.println("📅 Slot selection mail sent to: " + email);
                }
            }

            default -> System.out.println("⚠️ No email rule for status: " + status);
        }
    }

    public long getShortlistedCount() {
        return candidateRepository.countByStatus("Shortlisted");
    }

    public long getTotalUploadedResumes() {
        return candidateRepository.count();
    }

}