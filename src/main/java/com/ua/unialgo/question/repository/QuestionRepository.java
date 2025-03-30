package com.ua.unialgo.question.repository;

import com.ua.unialgo.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    Page<Question> findByTeacher(Pageable pageable);
}
