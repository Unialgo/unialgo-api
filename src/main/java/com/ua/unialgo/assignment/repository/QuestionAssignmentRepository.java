package com.ua.unialgo.assignment.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ua.unialgo.assignment.entity.QuestionAssignment;

public interface QuestionAssignmentRepository extends CrudRepository<QuestionAssignment, Long> {
    void deleteByAssignmentId(Long assignmentId);

    @Modifying
    @Query("DELETE FROM QuestionAssignment ql WHERE ql.assignment.id = :assignmentId AND ql.question.id = :questionId")
    void deleteByAssignmentIdAndQuestionId(Long assignmentId, Long questionId);
}
