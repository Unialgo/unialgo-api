package com.ua.unialgo.assignment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ua.unialgo.assignment.dto.*;
import com.ua.unialgo.assignment.entity.Assignment;
import com.ua.unialgo.assignment.entity.QuestionAssignment;
import com.ua.unialgo.assignment.repository.AssignmentRepository;
import com.ua.unialgo.assignment.repository.QuestionAssignmentRepository;
import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.question.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QuestionAssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;

    public QuestionAssignmentService(AssignmentRepository assignmentRepository,
            QuestionRepository questionRepository,
            QuestionAssignmentRepository questionAssignmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.questionRepository = questionRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
    }

    @Transactional
    public ResponseEntity<?> addQuestionToAssignment(Long assignmentId, AddQuestionsRequestDto dto) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + assignmentId));

        Question question = questionRepository.findById(dto.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + dto.questionId()));

        QuestionAssignment ql = new QuestionAssignment();
        ql.setAssignment(assignment);
        ql.setQuestion(question);
        ql.setAssignmentPosition(dto.index());

        questionAssignmentRepository.save(ql);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> updateQuestionsAssignment(Long assignmentId, UpdateQuestionAssignmentRequestDto dto) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + assignmentId));

        questionAssignmentRepository.deleteByAssignmentId(assignmentId);

        Iterable<Long> questionIds = dto.questions().stream()
                .map(QuestionPositionDto::questionId)
                .toList();

        Iterable<Question> questions = questionRepository.findAllById(questionIds);
        List<Question> questionList = new ArrayList<Question>();
        questions.forEach(questionList::add);
        
        Map<Long, Question> questionMap = questionList.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<QuestionAssignment> newMappings = dto.questions().stream()
                .map(qp -> {
                    QuestionAssignment ql = new QuestionAssignment();
                    ql.setAssignment(assignment);
                    ql.setQuestion(questionMap.get(qp.questionId()));
                    ql.setAssignmentPosition(qp.index());
                    return ql;
                }).toList();

        questionAssignmentRepository.saveAll(newMappings);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteQuestionFromAssignment(Long assignmentId, Long questionId) {
        questionAssignmentRepository.deleteByAssignmentIdAndQuestionId(assignmentId, questionId);
        return ResponseEntity.ok().build();
    }
}
