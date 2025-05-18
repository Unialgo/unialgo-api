package com.ua.unialgo.assignment.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
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

import com.ua.unialgo.assignment.dto.AddQuestionsRequestDto;
import com.ua.unialgo.assignment.dto.SaveAssignmentRequestDto;
import com.ua.unialgo.assignment.dto.UpdateQuestionAssignmentRequestDto;
import com.ua.unialgo.assignment.service.AssignmentService;
import com.ua.unialgo.assignment.service.QuestionAssignmentService;

@RestController
@RequestMapping(path = "/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final QuestionAssignmentService questionAssignmentService;

    public AssignmentController(AssignmentService assignmentService, QuestionAssignmentService questionAssignmentService) {
        this.assignmentService = assignmentService;
        this.questionAssignmentService = questionAssignmentService;
    }

    @GetMapping
    ResponseEntity<?> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<?> getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PostMapping
    ResponseEntity<?> createAssignment(@RequestBody SaveAssignmentRequestDto body, Principal principal) {
        return assignmentService.createAssignment(body, principal);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PutMapping(path = "/{id}")
    ResponseEntity<?> updateAssignmentById(@PathVariable Long id, @RequestBody SaveAssignmentRequestDto body) {
        return assignmentService.updateAssignmentById(id, body);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @DeleteMapping(path = "/{id}")
    ResponseEntity<?> deleteAssignmentById(@PathVariable Long id) {
        return assignmentService.deleteAssignmentById(id);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/assignments/{id}/question")
    public ResponseEntity<?> addQuestionToAssignment(@PathVariable Long id, @RequestBody AddQuestionsRequestDto body) {
        questionAssignmentService.addQuestionToAssignment(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PutMapping(path = "/{id}/questions")
    ResponseEntity<?> updateQuestionsAssignment(@PathVariable Long id, @RequestBody UpdateQuestionAssignmentRequestDto body) {
        return questionAssignmentService.updateQuestionsAssignment(id, body);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @DeleteMapping(path = "/{id}/{questionId}")
    ResponseEntity<?> deleteQuestionFromAssignment(@PathVariable Long id, @PathVariable Long questionId) {
        return questionAssignmentService.deleteQuestionFromAssignment(id, questionId);
    }

}
