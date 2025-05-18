package com.ua.unialgo.assignment.repository;
import org.springframework.data.repository.CrudRepository;

import com.ua.unialgo.assignment.entity.Assignment;

public interface AssignmentRepository extends CrudRepository<Assignment, Long> {}
