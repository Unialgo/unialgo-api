package com.ua.unialgo.list.controller;

import com.ua.unialgo.list.dto.SaveListRequestDto;
import com.ua.unialgo.list.service.ListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) { this.listService = listService; }

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

}
