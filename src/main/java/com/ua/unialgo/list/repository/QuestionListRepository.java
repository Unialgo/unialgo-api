package com.ua.unialgo.list.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ua.unialgo.list.entity.QuestionList;

public interface QuestionListRepository extends CrudRepository<QuestionList, Long> {
    void deleteByListId(Long listId);

    @Modifying
    @Query("DELETE FROM QuestionList ql WHERE ql.list.id = :listId AND ql.question.id = :questionId")
    void deleteByListIdAndQuestionId(Long listId, Long questionId);
}
