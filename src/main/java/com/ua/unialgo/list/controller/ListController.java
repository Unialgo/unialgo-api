package com.ua.unialgo.list.controller;

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

import com.ua.unialgo.list.dto.AddQuestionsRequestDto;
import com.ua.unialgo.list.dto.SaveListRequestDto;
import com.ua.unialgo.list.dto.UpdateQuestionListRequestDto;
import com.ua.unialgo.list.service.ListService;
import com.ua.unialgo.list.service.QuestionListService;

@RestController
@RequestMapping(path = "/lists")
public class ListController {

    private final ListService listService;
    private final QuestionListService questionListService;

    public ListController(ListService listService, QuestionListService questionListService) {
        this.listService = listService;
        this.questionListService = questionListService;
    }

    @GetMapping
    ResponseEntity<?> getAllLists() {
        return listService.getAllLists();
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<?> getListById(@PathVariable Long id) {
        return listService.getListById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PostMapping
    ResponseEntity<?> createList(@RequestBody SaveListRequestDto body, Principal principal) {
        return listService.createList(body, principal);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PutMapping(path = "/{id}")
    ResponseEntity<?> updateListById(@PathVariable Long id, @RequestBody SaveListRequestDto body) {
        return listService.updateListById(id, body);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @DeleteMapping(path = "/{id}")
    ResponseEntity<?> deleteListById(@PathVariable Long id) {
        return listService.deleteListById(id);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/lists/{id}/question")
    public ResponseEntity<?> addQuestionToList(@PathVariable Long id, @RequestBody AddQuestionsRequestDto body) {
        questionListService.addQuestionToList(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PutMapping(path = "/{id}/questions")
    ResponseEntity<?> updateQuestionsList(@PathVariable Long id, @RequestBody UpdateQuestionListRequestDto body) {
        return questionListService.updateQuestionsList(id, body);
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @DeleteMapping(path = "/{id}/{questionId}")
    ResponseEntity<?> deleteQuestionFromList(@PathVariable Long id, Long questionId) {
        return questionListService.deleteQuestionFromList(id, questionId);
    }

}
