package com.ua.unialgo.user.service;

import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
         this.teacherRepository = teacherRepository;
    }

    public Iterable<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
}
