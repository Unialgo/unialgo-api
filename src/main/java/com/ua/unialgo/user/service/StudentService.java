package com.ua.unialgo.user.service;

import com.ua.unialgo.user.entity.Student;
import com.ua.unialgo.user.repository.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * SOLID - Dependency Inversion Principle (DIP):
 * Na StudentService, a StudentRepository é injetado via construtor. Isso garante que o serviço dependa de
 * uma abstração (a interface StudentRepository) em vez de uma implementação concreta.
 */
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
         this.studentRepository = studentRepository;
    }

    public Iterable<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}