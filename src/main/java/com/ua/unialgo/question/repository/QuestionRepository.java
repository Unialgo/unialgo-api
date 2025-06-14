package com.ua.unialgo.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ua.unialgo.question.entity.Question;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    Iterable<Question> findAllByTeacherId(Long id);

    @Modifying
    @Query("SELECT q FROM Question q INNER JOIN QuestionAssignment ql ON ql.question.id = q.id WHERE ql.assignment.id = :assignmentId ORDER BY ql.assignmentPosition ASC")
    List<Question> findAllByAssignmentId(Long assignmentId);
}
