package com.ua.unialgo.user.controller;

import com.ua.unialgo.user.entity.Student;
import com.ua.unialgo.user.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @GetMapping
    Iterable<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
}
