package com.ua.unialgo.user.strategies;

import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.TeacherRepository;

public class TeacherRoleStrategy implements RoleStrategy {

    private final TeacherRepository teacherRepository;

    public TeacherRoleStrategy(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void handleRole(User user) {
        teacherRepository.findByUserId(user.getId()).orElseGet(() -> {
            Teacher teacher = new Teacher();

            teacher.setUser(user);

            return teacherRepository.save(teacher);
        });
    }
}
