package com.ua.unialgo.question.controller;

import com.ua.unialgo.question.dto.SaveQuestionRequestDto;
import com.ua.unialgo.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping(path = "/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) { this.questionService = questionService; }

    @GetMapping
    ResponseEntity<?> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PostMapping
    ResponseEntity<?> createQuestion(@RequestBody SaveQuestionRequestDto body, Principal principal) {
        return questionService.createQuestion(body, principal);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PutMapping(path = "/{id}")
    ResponseEntity<?> updateQuestionById(@PathVariable Long id, @RequestBody SaveQuestionRequestDto body) {
        return questionService.updateQuestionById(id, body);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @DeleteMapping(path = "/{id}")
    ResponseEntity<?> deleteQuestionById(@PathVariable Long id) {
        return questionService.deleteQuestionById(id);
    }

}
