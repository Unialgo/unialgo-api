package com.ua.unialgo.question.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ua.unialgo.question.dto.SaveQuestionRequestDto;
import com.ua.unialgo.question.service.QuestionService;

@RestController
@RequestMapping(path = "/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) { this.questionService = questionService; }

    @GetMapping
    ResponseEntity<?> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping(path = "/{id}/assignment")
    ResponseEntity<?> getAllQuestionsByAssignmentId(@PathVariable Long id) {
        return questionService.getAllQuestionsByAssignmentId(id);
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
