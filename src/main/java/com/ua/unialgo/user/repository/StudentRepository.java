package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * SOLID - Open/Closed Principle (OCP):
 * A interface StudentRepository estende a CrudRepository, que fornece operações básicas de CRUD.
 * Se for necessário adicionar consultas específicas (por exemplo, findByUserId), você pode estender
 * a interface sem modificar a implementação existente do CrudRepository.
 */
public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByUserId(String id);
}
