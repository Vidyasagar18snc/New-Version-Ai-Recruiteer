package com.Vendor.service;

import com.Vendor.dto.FeedbackRequestDTO;
import com.Vendor.dto.FeedbackResponseDTO;
import com.Vendor.model.Feedback;
import com.Vendor.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;

    // ✅ SAVE FEEDBACK (your existing logic)
    public Feedback saveFeedback(FeedbackRequestDTO dto) {

        double score = calculateScore(dto);

        String decision = generateDecision(score);
        String strengths = generateStrengths(dto);
        String weaknesses = generateWeaknesses(dto);
        String summary = generateSummary(dto, score);

        Feedback feedback = Feedback.builder()
                .name(dto.getName())
                .role(dto.getRole())
                .experience(dto.getExperience())
                .round(dto.getRound())
                .technical(dto.getTechnical())
                .communication(dto.getCommunication())
                .problemSolving(dto.getProblemSolving())
                .score(score)
                .decision(decision)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .summary(summary)
                .createdBy(dto.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(feedback);
    }

    // ✅ NEW: DASHBOARD DATA (IMPORTANT 🔥)
    public List<FeedbackResponseDTO> getDashboardData() {

        List<Feedback> list = repository.findAll();

        // 🔥 Sort by score (high to low)
        list.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        List<FeedbackResponseDTO> response = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            Feedback f = list.get(i);

            double percentage = (f.getScore() / 5.0) * 100;

            response.add(
                    FeedbackResponseDTO.builder()
                            .name(f.getName())
                            .role(f.getRole())
                            .experience(f.getExperience())
                            .percentageScore(Math.round(percentage * 10.0) / 10.0) // ✅ 68.2%
                            .rank(i + 1) // ✅ #1, #2, #3
                            .decision(f.getDecision())
                            .summary(f.getSummary())
                            .createdBy(f.getCreatedBy())
                            .build()
            );
        }

        return response;
    }

    // =========================
    // EXISTING LOGIC (UNCHANGED)
    // =========================

    private double calculateScore(FeedbackRequestDTO dto) {
        return (dto.getTechnical() * 0.4) +
                (dto.getCommunication() * 0.3) +
                (dto.getProblemSolving() * 0.3);
    }

    private String generateDecision(double score) {
        if (score >= 4.0) return "STRONG_HIRE";
        if (score >= 3.5) return "HIRE";
        if (score >= 3.0) return "HOLD";
        return "REJECT";
    }

    private String generateStrengths(FeedbackRequestDTO dto) {

        StringBuilder s = new StringBuilder();

        if (dto.getTechnical() >= 4)
            s.append("Strong technical skills, ");

        if (dto.getProblemSolving() >= 4)
            s.append("good problem-solving ability, ");

        if (dto.getCommunication() >= 4)
            s.append("excellent communication, ");

        if (s.length() == 0)
            return "No major strengths observed";

        return s.toString().replaceAll(", $", "");
    }

    private String generateWeaknesses(FeedbackRequestDTO dto) {

        StringBuilder w = new StringBuilder();

        if (dto.getTechnical() < 3)
            w.append("weak technical skills, ");
        if (dto.getCommunication() < 3)
            w.append("poor communication, ");

        if (dto.getProblemSolving() < 3)
            w.append("needs improvement in problem-solving, ");

        if (w.length() == 0)
            return "No major weaknesses";

        return w.toString().replaceAll(", $", "");
    }

    private String generateSummary(FeedbackRequestDTO dto, double score) {

        StringBuilder summary = new StringBuilder();

        if (dto.getTechnical() >= 4 && dto.getProblemSolving() >= 4) {
            summary.append("Candidate shows strong technical and problem-solving skills. ");
        }

        if (dto.getCommunication() < 3) {
            summary.append("Communication needs improvement. ");
        }

        if (score >= 4.0) {
            summary.append("Overall, highly suitable for the role.");
        } else if (score >= 3.5) {
            summary.append("Overall, a good candidate with minor improvements needed.");
        } else if (score >= 3.0) {
            summary.append("Average performance, needs improvement in problem solving.");
        } else {
            summary.append("Not suitable for this role.");
        }

        return summary.toString();
    }


    public List<Feedback> getAllFeedback() {
        return repository.findAll();
    }
}