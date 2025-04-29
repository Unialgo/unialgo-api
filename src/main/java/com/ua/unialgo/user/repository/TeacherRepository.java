package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    Optional<Teacher> findByUserId(String id);
}
