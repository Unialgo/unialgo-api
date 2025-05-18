package com.ua.unialgo.list.service;

import java.util.Map;
import java.util.stream.Collectors;

import com.ua.unialgo.list.dto.*;
import com.ua.unialgo.list.entity.List;
import com.ua.unialgo.list.entity.QuestionList;
import com.ua.unialgo.list.repository.ListRepository;
import com.ua.unialgo.list.repository.QuestionListRepository;
import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.question.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QuestionListService {

    private final ListRepository listRepository;
    private final QuestionRepository questionRepository;
    private final QuestionListRepository questionListRepository;

    public QuestionListService(ListRepository listRepository,
            QuestionRepository questionRepository,
            QuestionListRepository questionListRepository) {
        this.listRepository = listRepository;
        this.questionRepository = questionRepository;
        this.questionListRepository = questionListRepository;
    }

    @Transactional
    public ResponseEntity<?> addQuestionToList(Long listId, AddQuestionsRequestDto dto) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("List not found with id: " + listId));

        Question question = questionRepository.findById(dto.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + dto.questionId()));

        QuestionList ql = new QuestionList();
        ql.setList(list);
        ql.setQuestion(question);
        ql.setListPosition(dto.index());

        questionListRepository.save(ql);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> updateQuestionsList(Long listId, UpdateQuestionListRequestDto dto) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("List not found with id: " + listId));

        questionListRepository.deleteByListId(listId);

        Iterable<Long> questionIds = dto.questions().stream()
                .map(QuestionPositionDto::questionId)
                .toList();

        Iterable<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        Iterable<QuestionList> newMappings = dto.questions().stream()
                .map(qp -> {
                    QuestionList ql = new QuestionList();
                    ql.setList(list);
                    ql.setQuestion(questionMap.get(qp.questionId()));
                    ql.setListPosition(qp.index());
                    return ql;
                }).toList();

        questionListRepository.saveAll(newMappings);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteQuestionFromList(Long listId, Long questionId) {
        questionListRepository.deleteByListIdAndQuestionId(listId, questionId);
        return ResponseEntity.ok().build();
    }
}
