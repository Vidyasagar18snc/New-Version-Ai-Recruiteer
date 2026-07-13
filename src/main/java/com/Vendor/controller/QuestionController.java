package com.Vendor.controller;
import com.Vendor.dto.QuestionDTO;
import com.Vendor.model.Question;
import com.Vendor.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/questions")
    public List<QuestionDTO> getQuestions() {
        return questionService.getAllQuestions();
    }


    @PostMapping("/add")
    public Question addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }
    @PostMapping("/add-bulk")
    public List<Question> addBulk(@RequestBody List<Question> questions) {
        return questionService.addBulkQuestions(questions);
    }
    @PostMapping("/upload-questions")
    public String upload(@RequestParam("file") MultipartFile file) {
        questionService.uploadQuestions(file);
        return "Bulk questions uploaded successfully!";
    }
}