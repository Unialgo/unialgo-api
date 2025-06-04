package com.ua.unialgo.submission.service;

import com.ua.unialgo.judge0.service.Judge0Service;
import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.question.entity.TestCase;
import com.ua.unialgo.question.service.QuestionService;
import com.ua.unialgo.submission.dto.SubmitCodeRequestDto;
import com.ua.unialgo.submission.entity.Submission;
import com.ua.unialgo.submission.entity.SubmissionStatus;
import com.ua.unialgo.submission.repository.SubmissionRepository;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    
    @Mock
    private Judge0Service judge0Service;
    
    @Mock
    private QuestionService questionService;
    
    @Mock
    private UserService userService;

    @InjectMocks
    private SubmissionService submissionService;

    private User testUser;
    private Question testQuestion;
    private SubmitCodeRequestDto testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setTitle("Test Question");
        testQuestion.setTestCases(List.of(
                createTestCase("1", "1"),
                createTestCase("2", "2")
        ));

        testRequest = new SubmitCodeRequestDto();
        testRequest.setQuestionId(1L);
        testRequest.setSourceCode("print('Hello World')");
        testRequest.setLanguageId(71); // Python
    }

    @Test
    void submitCode_ShouldCreateSubmission() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);
        when(questionService.findById(1L)).thenReturn(testQuestion);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission submission = invocation.getArgument(0);
            submission.setId(1L);
            return submission;
        });

        // Act
        Submission result = submissionService.submitCode(testRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getUser());
        assertEquals(testQuestion, result.getQuestion());
        assertEquals("print('Hello World')", result.getSourceCode());
        assertEquals(Integer.valueOf(71), result.getLanguageId());
        assertEquals(SubmissionStatus.PENDING, result.getStatus());
        assertEquals(Integer.valueOf(2), result.getTotalTestCases());

        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void findById_ShouldReturnSubmission_WhenExists() {
        // Arrange
        Submission testSubmission = createTestSubmission();
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        // Act
        Submission result = submissionService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(submissionRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class, 
                    () -> submissionService.findById(1L));
    }

    @Test
    void toDto_ShouldConvertCorrectly() {
        // Arrange
        Submission submission = createTestSubmission();

        // Act
        var dto = submissionService.toDto(submission, false);

        // Assert
        assertNotNull(dto);
        assertEquals(submission.getId(), dto.getId());
        assertEquals(submission.getUser().getId(), dto.getUserId());
        assertEquals(submission.getQuestion().getId(), dto.getQuestionId());
        assertEquals(submission.getStatus(), dto.getStatus());
        assertNull(dto.getSourceCode()); // Should not include source code when false
    }

    @Test
    void toDto_ShouldIncludeSourceCode_WhenRequested() {
        // Arrange
        Submission submission = createTestSubmission();

        // Act
        var dto = submissionService.toDto(submission, true);

        // Assert
        assertNotNull(dto);
        assertEquals(submission.getSourceCode(), dto.getSourceCode());
    }

    private Submission createTestSubmission() {
        Submission submission = new Submission();
        submission.setId(1L);
        submission.setUser(testUser);
        submission.setQuestion(testQuestion);
        submission.setSourceCode("print('Hello World')");
        submission.setLanguageId(71);
        submission.setStatus(SubmissionStatus.ACCEPTED);
        submission.setScore(100.0f);
        submission.setTestCasesPassed(2);
        submission.setTotalTestCases(2);
        return submission;
    }

    private TestCase createTestCase(String input, String expectedOutput) {
        TestCase testCase = new TestCase();
        testCase.setId(Long.parseLong(input));
        testCase.setInput(input);
        testCase.setExpectedOutput(expectedOutput);
        testCase.setExample(false);
        return testCase;
    }
}