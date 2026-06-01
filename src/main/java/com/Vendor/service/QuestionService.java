package com.Vendor.service;
import com.Vendor.dto.QuestionDTO;
import com.Vendor.model.Question;
import com.Vendor.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository repository;

    // ✅ Get all questions (without answers)
    public List<QuestionDTO> getAllQuestions() {
        List<Question> questions = repository.findAll();

        // 🔥 Shuffle questions (optional but recommended)
        Collections.shuffle(questions);

        return questions.stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getQuestion(),
                        q.getOptions()
                ))
                .collect(Collectors.toList());
    }

    // ✅ Add question (for admin)
    public Question addQuestion(Question question) {
        return repository.save(question);
    }
    public List<Question> addBulkQuestions(List<Question> questions) {
        return repository.saveAll(questions);
    }
    public void uploadQuestions(MultipartFile file) {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            List<Question> questions = reader.lines()
                    .skip(1) // skip header
                    .map(line -> {
                        String[] data = line.split(",");

                        return Question.builder()
                                .question(data[0])
                                .options(List.of(data[1], data[2], data[3], data[4]))
                                .correctAnswer(data[5])
                                .build();
                    })
                    .collect(Collectors.toList());

            repository.saveAll(questions);

        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }
}