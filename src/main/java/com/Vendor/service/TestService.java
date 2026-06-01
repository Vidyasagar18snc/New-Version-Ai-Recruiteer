package com.Vendor.service;
import com.Vendor.dto.*;
import com.Vendor.model.*;
import com.Vendor.repository.CandidateRepository;
import com.Vendor.repository.QuestionRepository;
import com.Vendor.repository.ResultRepository;
import com.Vendor.repository.TestLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestLinkRepository repository;
    private final QuestionRepository questionRepository;
    private final EmailService emailService;
    private final CandidateRepository candidateRepository;
    private final ResultRepository resultRepository;
    private final PanelService panelService;

    @Value("${app.interview.base-url}")
    private String testBaseUrl;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(4);

    public String generateTestLink(GenerateLinkRequestDTO request) {

        try {
            System.out.println("📥 Generating link for: " + request.getCandidateId());

            Optional<TestLink> existing =
                    repository.findByCandidateId(request.getCandidateId());

            if (existing.isPresent()) {
                TestLink link = existing.get();

                if (!link.isAttempted()
                        && link.getExpiryTime().isAfter(LocalDateTime.now())) {

                    System.out.println("✅ Existing link reused");
                    return testBaseUrl + "?token=" + link.getToken();
                }
            }

            String token = UUID.randomUUID().toString();

            Candidate candidate = candidateRepository.findById(request.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            TestLink link = TestLink.builder()
                    .candidateId(candidate.getId())
                    .candidateEmail(candidate.getEmail()) // ✅ correct
                    .testId(request.getTestId())
                    .token(token)
                    .attempted(false)
                    .expiryTime(LocalDateTime.now().plusHours(48))
                    .build();
            repository.save(link);

            System.out.println("✅ New test link created");

            return testBaseUrl + "?token=" + token;

        } catch (Exception e) {
            System.err.println("❌ Error in generateTestLink()");
            e.printStackTrace();
            throw new RuntimeException("Failed to generate test link");
        }
    }


    public ValidateResponseDTO validateToken(String token) {

        try {
            Optional<TestLink> opt = repository.findByToken(token);

            if (opt.isEmpty()) {
                return ValidateResponseDTO.builder()
                        .valid(false)
                        .message("Invalid link")
                        .build();
            }

            TestLink link = opt.get();

            if (link.isAttempted()) {
                return ValidateResponseDTO.builder()
                        .valid(false)
                        .message("Test already attempted")
                        .build();
            }

            if (link.getExpiryTime().isBefore(LocalDateTime.now())) {
                return ValidateResponseDTO.builder()
                        .valid(false)
                        .message("Link expired")
                        .build();
            }

            if (link.getStartTime() == null) {
                link.setStartTime(System.currentTimeMillis());
                repository.save(link);
            }

            return ValidateResponseDTO.builder()
                    .valid(true)
                    .candidateId(link.getCandidateId())
                    .testId(link.getTestId())
                    .duration(30)
                    .build();

        } catch (Exception e) {
            System.err.println("❌ Error in validateToken()");
            e.printStackTrace();
            throw new RuntimeException("Token validation failed");
        }
    }

    @Transactional
    public TestResultResponse evaluateTest(TestSubmissionRequest request){

        try{
            TestLink link=repository.findByToken(request.getToken())
                    .orElseThrow(()-> new RuntimeException("Invalid token"));
            if(link.isAttempted()){

                throw new RuntimeException("Test already attempted");
            }
            if(link.getExpiryTime()
                    .isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Link expired");
            }
            long endTime= System.currentTimeMillis();
            long startTime= (link.getStartTime()!=null)
                            ?link.getStartTime()
                            :endTime;
            long duration= (endTime-startTime)/1000;
            link.setEndTime(endTime);
            link.setDuration(duration);
            int score=0;
            int total= request.getAnswers().size();
            for(AnswerDTO ans:request.getAnswers()){

                try{

                    Question q= questionRepository.findById(ans.getQuestionId())
                                    .orElse(null);
                    if(q!=null
                            &&q.getCorrectAnswer()
                            .equalsIgnoreCase(
                                    ans.getSelectedAnswer()
                            )){

                        score++;
                    }

                }catch(Exception e){

                    System.err.println("Error in question : " +ans.getQuestionId()
                    );
                    e.printStackTrace();
                }
            }
            double percentage=
                    (total==0)
                            ?0
                            :((double)score/total)*100;

            String status=
                    (percentage>=80)
                            ?"TOP_PERFORMER"
                            :(percentage>=70)
                            ?"PASS"
                            :(percentage>=50)
                            ?"REVIEW"
                            :"FAIL";

            link.setScore(score);
            link.setAttempted(true);
            repository.save(link);
            Candidate candidate= candidateRepository.findById(link.getCandidateId())
                            .orElse(null);

            String candidateName= (candidate!=null
                            &&candidate.getName()!=null)
                            ?candidate.getName()
                            :"Candidate";

            ZoneId zone= ZoneId.systemDefault();
            TestResult result= TestResult.builder().candidateId(link.getCandidateId())
                            .candidateName(candidateName)
                            .testId(link.getTestId())
                            .score(score)
                            .percentage(percentage)
                            .timeTaken(duration)
                            .startTime(Instant.ofEpochMilli(startTime)
                                            .atZone(zone)
                                            .toLocalDateTime()
                            )
                            .endTime(
                                    Instant.ofEpochMilli(endTime)
                                            .atZone(zone)
                                            .toLocalDateTime()
                            )
                            .build();

            resultRepository.save(result);

            if("PASS".equals(status)
                    ||"TOP_PERFORMER".equals(status)){

                if(candidate!=null){

                    System.out.println("Candidate Role : " +candidate.getRole());
                    candidate.setStatus("WAITING_FOR_PANEL_SLOT_SELECTION");

                    List<Panel> panels= panelService.assignPanel(candidate.getRole());

                    System.out.println("Panels Found : " +(panels!=null ?panels.size() :0));
                    if(panels==null ||panels.isEmpty()){

                        System.out.println("No panel found for role : " +candidate.getRole());
                        candidate.setStatus("WAITING_FOR_PANEL_ASSIGNMENT");
                        candidateRepository.save(candidate);
                    }else{
                        Panel panel=panels.get(0);
                        candidate.setAssignedPanelId(panel.getId());
                        candidateRepository.save(candidate);

                        emailService.sendInterviewerSlotSelectionMail(
                                        panel.getEmail(),
                                        panel.getName(),
                                        panel.getPassword(),
                                        candidate.getName(),
                                        candidate.getRole()
                                );
                        System.out.println("Panel assigned successfully");
                    }
                }
            }else if("FAIL".equals(status)){

                if(candidate!=null){

                    candidate.setStatus("TEST_FAILED");
                    candidateRepository.save(candidate);
                }
            }

            int rank= calculateRank(link.getTestId(),
                            link.getCandidateId());

            TestResultResponse response= TestResultResponse.builder()
                            .score(score)
                            .total(total)
                            .percentage(percentage)
                            .status(status)
                            .rank(rank)
                            .build();

            sendResultEmailWithHandling(
                    link,
                    score,
                    total,
                    percentage,
                    rank,
                    status
            );

            return response;

        }catch(Exception e){

            System.err.println("Error in evaluateTest()");

            e.printStackTrace();
            throw new RuntimeException("Evaluation failed : " +e.getMessage(), e);
        }
    }    private void sendResultEmailWithHandling(TestLink link,
                                                  int score,
                                                  int total,
                                                  double percentage,
                                                  int rank,
                                                  String status) {

        final String emailTo = link.getCandidateEmail();
        System.out.println("📧 Email: " + emailTo);
        if (emailTo == null || !emailTo.contains("@")) {
            System.out.println("❌ Invalid email, skipping...");
            return;
        }

        scheduler.schedule(() -> {
            try {
                System.out.println("📤 Sending email...");

                int latestRank = calculateRank(
                        link.getTestId(), link.getCandidateId());

                //  FETCH REAL NAME
                Candidate candidate = candidateRepository
                        .findById(link.getCandidateId())
                        .orElse(null);

                String name = (candidate != null && candidate.getName() != null)
                        ? candidate.getName()
                        : "Candidate";  // fallback

                System.out.println("👤 Candidate Name: " + name);

                // USE REAL NAME HERE
                emailService.sendResultEmail(
                        emailTo,
                        name,   // 🔥 FIXED
                        score,
                        total,
                        percentage,
                        latestRank,
                        status
                );

                System.out.println(" Email sent!");

            } catch (Exception e) {
                System.err.println("❌ Email failed!");
                e.printStackTrace();
            }

        }, 1, TimeUnit.MINUTES);
    }

    public int calculateRank(String testId, String candidateId) {

        try {

            List<TestLink> all = repository.findByTestId(testId);

            if (all == null || all.isEmpty()) return 1;

            List<TestLink> attempted = all.stream()
                    .filter(TestLink::isAttempted)
                    .sorted((a, b) -> {

                        int sA = (a.getScore() != null) ? a.getScore() : 0;
                        int sB = (b.getScore() != null) ? b.getScore() : 0;

                        if (sB != sA) return Integer.compare(sB, sA);

                        long dA = (a.getDuration() != null) ? a.getDuration() : Long.MAX_VALUE;
                        long dB = (b.getDuration() != null) ? b.getDuration() : Long.MAX_VALUE;

                        return Long.compare(dA, dB);
                    })
                    .toList();

            int rank = 1;

            for (TestLink t : attempted) {
                if (candidateId.equals(t.getCandidateId())) {
                    return rank;
                }
                rank++;
            }

            return rank;

        } catch (Exception e) {
            System.err.println("❌ Error in calculateRank()");
            e.printStackTrace();
            return 0;
        }
    }
    public List<TestResult> getAllRankedCandidates() {

        // STEP 1: Fetch all results
        List<TestResult> results = resultRepository.findAll();

        if (results.isEmpty()) {
            return Collections.emptyList();
        }


        results.sort(
                Comparator.comparingInt(TestResult::getScore).reversed()
                        .thenComparingLong(TestResult::getTimeTaken)
        );


        int rank = 1;

        for (int i = 0; i < results.size(); i++) {

            if (i > 0) {
                TestResult prev = results.get(i - 1);
                TestResult curr = results.get(i);

                boolean isSameScore = curr.getScore() == prev.getScore();
                boolean isSameTime = curr.getTimeTaken() == prev.getTimeTaken();

                //  Only same score + same time → same rank
                if (!(isSameScore && isSameTime)) {
                    rank = i + 1;
                }
            }

            results.get(i).setRank(rank);
        }

        //  STEP 4: Persist ranks (optional but recommended)
        resultRepository.saveAll(results);

        return results;
    }
}