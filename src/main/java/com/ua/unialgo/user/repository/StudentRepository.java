package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {}
