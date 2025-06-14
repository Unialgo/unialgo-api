package com.ua.unialgo.user.strategies;

import com.ua.unialgo.user.repository.StudentRepository;
import com.ua.unialgo.user.repository.TeacherRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RoleStrategyFactory {

    private final Map<String, RoleStrategy> strategies;

    public RoleStrategyFactory(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.strategies = Map.of(
                "STUDENT", new StudentRoleStrategy(studentRepository),
                "TEACHER", new TeacherRoleStrategy(teacherRepository)
        );
    }

    public RoleStrategy getStrategy(String role) {
        return strategies.getOrDefault(role, user -> {
            throw new IllegalArgumentException("Unsupported role: " + role);
        });
    }
}
