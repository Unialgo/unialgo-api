package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByUserId(String id);
}
