package com.ua.unialgo.user.controller;

import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.service.TeacherService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = "/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    Iterable<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }
}
