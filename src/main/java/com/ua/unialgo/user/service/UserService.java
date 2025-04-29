package com.ua.unialgo.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ua.unialgo.user.entity.Student;
import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.StudentRepository;
import com.ua.unialgo.user.repository.TeacherRepository;
import com.ua.unialgo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public UserService(UserRepository userRepository, TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public void syncUser(String id, String username, String role) {
        User user = new User(id, username);

        userRepository.findById(id).orElseGet(() -> userRepository.save(user));

        if (role.equals("STUDENT")) {
            studentRepository.findByUserId(id).orElseGet(() -> {
                Student student = new Student();

                student.setUser(user);

                return studentRepository.save(student);
            });
        }

        if (role.equals("TEACHER")) {
            teacherRepository.findByUserId(id).orElseGet(() -> {
                Teacher teacher = new Teacher();

                teacher.setUser(user);

                return teacherRepository.save(teacher);
            });
        }
    }
}
