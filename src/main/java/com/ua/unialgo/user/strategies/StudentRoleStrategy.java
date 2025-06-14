package com.ua.unialgo.user.strategies;

import com.ua.unialgo.user.entity.Student;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.StudentRepository;

public class StudentRoleStrategy implements RoleStrategy {

    private final StudentRepository studentRepository;

    public StudentRoleStrategy(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void handleRole(User user) {
        studentRepository.findByUserId(user.getId()).orElseGet(() -> {
            Student student = new Student();

            student.setUser(user);

            return studentRepository.save(student);
        });
    }
}
