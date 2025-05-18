package com.ua.unialgo.question.service;

import com.ua.unialgo.question.dto.SaveQuestionRequestDto;
import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.question.repository.QuestionRepository;
import com.ua.unialgo.user.entity.Teacher;
import com.ua.unialgo.user.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TeacherRepository teacherRepository;

    public QuestionService(QuestionRepository questionRepository, TeacherRepository teacherRepository) {
        this.questionRepository = questionRepository;
        this.teacherRepository = teacherRepository;
    }

    public ResponseEntity<?> getAllQuestions() {
        Iterable<Question> questions = questionRepository.findAll();

        return ResponseEntity.ok(questions);
    }

    public ResponseEntity<?> getAllQuestionsByAssignmentId(Long id) {
        Iterable<Question> questions = questionRepository.findAllByAssignmentId(id);

        return ResponseEntity.ok(questions);
    }

    public ResponseEntity<?> getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);

        return ResponseEntity.ok(question);
    }

    public ResponseEntity<?> createQuestion(SaveQuestionRequestDto saveQuestionRequest, Principal principal) {
        Question question = new Question();

        String userId = principal.getName();

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id " + userId));;

        question.setTitle(saveQuestionRequest.title());
        question.setStatement(saveQuestionRequest.statement());
        question.setTeacher(teacher);

        Question questionInDb = questionRepository.save(question);

        return ResponseEntity.ok(questionInDb.getId());
    }

    public ResponseEntity<?> updateQuestionById(Long id, SaveQuestionRequestDto saveQuestionRequest) {
        Question questionInDb = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id " + id));;

        questionInDb.setTitle(saveQuestionRequest.title());
        questionInDb.setStatement(saveQuestionRequest.statement());

        questionRepository.save(questionInDb);

        return ResponseEntity.ok(questionInDb.getId());
    }

    public ResponseEntity<?> deleteQuestionById(Long id) {
        questionRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
