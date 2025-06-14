package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * SOLID - Interface Segregation Principle (ISP):
 * A interface CrudRepository fornece um conjunto mínimo de métodos para operações CRUD, permitindo
 * que a TeacherRepository defina uma interface mais específica para os seus requisitos.
 */
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    Optional<Teacher> findByUserId(String id);
}
