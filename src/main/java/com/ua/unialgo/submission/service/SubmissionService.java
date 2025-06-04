package com.ua.unialgo.submission.service;

import com.ua.unialgo.judge0.dto.Judge0StatusResponseDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionRequestDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionResponseDto;
import com.ua.unialgo.judge0.service.Judge0Service;
import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.question.entity.TestCase;
import com.ua.unialgo.question.service.QuestionService;
import com.ua.unialgo.submission.dto.SubmitCodeRequestDto;
import com.ua.unialgo.submission.dto.SubmissionResponseDto;
import com.ua.unialgo.submission.entity.Submission;
import com.ua.unialgo.submission.entity.SubmissionStatus;
import com.ua.unialgo.submission.repository.SubmissionRepository;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for managing code submissions.
 * Follows the existing service patterns and integrates with Judge0Service.
 */
@Service
@Transactional
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final Judge0Service judge0Service;
    private final QuestionService questionService;
    private final UserService userService;

    public SubmissionService(SubmissionRepository submissionRepository,
                           Judge0Service judge0Service,
                           QuestionService questionService,
                           UserService userService) {
        this.submissionRepository = submissionRepository;
        this.judge0Service = judge0Service;
        this.questionService = questionService;
        this.userService = userService;
    }

    /**
     * Submits code for evaluation.
     * 
     * @param request The submission request
     * @param userId The user ID
     * @return The created submission
     */
    public Submission submitCode(SubmitCodeRequestDto request, Long userId) {
        logger.info("Processing code submission for user {} and question {}", 
                   userId, request.getQuestionId());

        // Validate and get entities
        User user = userService.findById(userId);
        Question question = questionService.findById(request.getQuestionId());

        // Validate language is allowed for this question
        validateLanguage(question, request.getLanguageId());

        // Create submission entity
        Submission submission = new Submission(user, question, 
                                             request.getSourceCode(), 
                                             request.getLanguageId());
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setTotalTestCases(question.getTestCases().size());

        // Save initial submission
        submission = submissionRepository.save(submission);
        logger.info("Created submission with ID: {}", submission.getId());

        // Submit to Judge0 asynchronously
        CompletableFuture.runAsync(() -> processSubmissionAsync(submission));

        return submission;
    }

    /**
     * Finds submission by ID.
     * 
     * @param submissionId The submission ID
     * @return The submission
     * @throws EntityNotFoundException if submission not found
     */
    @Transactional(readOnly = true)
    public Submission findById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with ID: " + submissionId));
    }

    /**
     * Finds submissions by user ID with pagination.
     * 
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    @Transactional(readOnly = true)
    public Page<Submission> findByUserId(Long userId, Pageable pageable) {
        return submissionRepository.findByUserId(userId, pageable);
    }

    /**
     * Finds submissions by question ID with pagination.
     * 
     * @param questionId The question ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    @Transactional(readOnly = true)
    public Page<Submission> findByQuestionId(Long questionId, Pageable pageable) {
        return submissionRepository.findByQuestionId(questionId, pageable);
    }

    /**
     * Finds submissions by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    @Transactional(readOnly = true)
    public Page<Submission> findByUserAndQuestion(Long userId, Long questionId, Pageable pageable) {
        return submissionRepository.findByUserIdAndQuestionId(userId, questionId, pageable);
    }

    /**
     * Gets the latest submission for a user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return The latest submission or null
     */
    @Transactional(readOnly = true)
    public Submission getLatestSubmission(Long userId, Long questionId) {
        return submissionRepository.findLatestSubmissionByUserAndQuestion(userId, questionId)
                .orElse(null);
    }

    /**
     * Gets the best submission (highest score) for a user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return The best submission or null
     */
    @Transactional(readOnly = true)
    public Submission getBestSubmission(Long userId, Long questionId) {
        return submissionRepository.findBestSubmissionByUserAndQuestion(userId, questionId)
                .orElse(null);
    }

    /**
     * Updates submission status from Judge0 response.
     * 
     * @param submissionId The submission ID
     * @return Updated submission
     */
    public Submission updateSubmissionStatus(Long submissionId) {
        Submission submission = findById(submissionId);
        
        if (submission.getJudge0Token() == null) {
            logger.warn("Cannot update status for submission {} - no Judge0 token", submissionId);
            return submission;
        }

        try {
            Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                    submission.getJudge0Token(), false);
            
            updateSubmissionFromJudge0Status(submission, status);
            return submissionRepository.save(submission);
            
        } catch (Exception e) {
            logger.error("Error updating status for submission {}: {}", submissionId, e.getMessage());
            submission.setStatus(SubmissionStatus.INTERNAL_ERROR);
            submission.setMessage("Error retrieving status from Judge0");
            return submissionRepository.save(submission);
        }
    }

    /**
     * Processes all submissions that need status updates.
     * Used by scheduled tasks.
     */
    public void processStatusUpdates() {
        List<Submission> submissionsToUpdate = submissionRepository.findSubmissionsNeedingStatusUpdate();
        
        logger.info("Processing status updates for {} submissions", submissionsToUpdate.size());
        
        for (Submission submission : submissionsToUpdate) {
            try {
                updateSubmissionStatus(submission.getId());
            } catch (Exception e) {
                logger.error("Error updating submission {}: {}", submission.getId(), e.getMessage());
            }
        }
    }

    /**
     * Converts Submission entity to DTO.
     * 
     * @param submission The submission entity
     * @param includeSourceCode Whether to include source code
     * @return The submission DTO
     */
    @Transactional(readOnly = true)
    public SubmissionResponseDto toDto(Submission submission, boolean includeSourceCode) {
        SubmissionResponseDto dto = new SubmissionResponseDto();
        
        dto.setId(submission.getId());
        dto.setUserId(submission.getUser().getId());
        dto.setUserEmail(submission.getUser().getEmail());
        dto.setQuestionId(submission.getQuestion().getId());
        dto.setQuestionTitle(submission.getQuestion().getTitle());
        dto.setLanguageId(submission.getLanguageId());
        dto.setLanguageName(getLanguageName(submission.getLanguageId()));
        dto.setStatus(submission.getStatus());
        dto.setStdout(submission.getStdout());
        dto.setStderr(submission.getStderr());
        dto.setCompileOutput(submission.getCompileOutput());
        dto.setMessage(submission.getMessage());
        dto.setExitCode(submission.getExitCode());
        dto.setTime(submission.getTime());
        dto.setWallTime(submission.getWallTime());
        dto.setMemory(submission.getMemory());
        dto.setTestCasesPassed(submission.getTestCasesPassed());
        dto.setTotalTestCases(submission.getTotalTestCases());
        dto.setScore(submission.getScore());
        dto.setSubmissionDate(submission.getSubmissionDate());
        dto.setFinishedAt(submission.getFinishedAt());
        
        if (includeSourceCode) {
            dto.setSourceCode(submission.getSourceCode());
        }
        
        return dto;
    }

    /**
     * Processes submission asynchronously.
     * 
     * @param submission The submission to process
     */
    private void processSubmissionAsync(Submission submission) {
        try {
            logger.info("Starting async processing for submission {}", submission.getId());
            
            // Update status to evaluating
            submission.setStatus(SubmissionStatus.EVALUATING);
            submissionRepository.save(submission);

            // Process test cases
            evaluateSubmission(submission);
            
        } catch (Exception e) {
            logger.error("Error processing submission {}: {}", submission.getId(), e.getMessage(), e);
            
            submission.setStatus(SubmissionStatus.INTERNAL_ERROR);
            submission.setMessage("Internal error during evaluation: " + e.getMessage());
            submission.setFinishedAt(LocalDateTime.now());
            submissionRepository.save(submission);
        }
    }

    /**
     * Evaluates submission against test cases.
     * 
     * @param submission The submission to evaluate
     */
    private void evaluateSubmission(Submission submission) {
        Question question = submission.getQuestion();
        List<TestCase> testCases = question.getTestCases();
        
        if (testCases.isEmpty()) {
            logger.warn("No test cases found for question {}", question.getId());
            submission.setStatus(SubmissionStatus.INTERNAL_ERROR);
            submission.setMessage("No test cases configured for this question");
            return;
        }

        int passedTests = 0;
        StringBuilder outputLog = new StringBuilder();
        
        for (TestCase testCase : testCases) {
            try {
                boolean passed = evaluateTestCase(submission, testCase, outputLog);
                if (passed) {
                    passedTests++;
                }
            } catch (Exception e) {
                logger.error("Error evaluating test case {}: {}", testCase.getId(), e.getMessage());
                submission.setStatus(SubmissionStatus.INTERNAL_ERROR);
                submission.setMessage("Error during test case evaluation");
                submission.setFinishedAt(LocalDateTime.now());
                submissionRepository.save(submission);
                return;
            }
        }

        // Update final results
        submission.setTestCasesPassed(passedTests);
        submission.calculateScore();
        submission.setStdout(outputLog.toString());
        
        if (passedTests == testCases.size()) {
            submission.setStatus(SubmissionStatus.ACCEPTED);
        } else {
            submission.setStatus(SubmissionStatus.WRONG_ANSWER);
        }
        
        submission.setFinishedAt(LocalDateTime.now());
        submissionRepository.save(submission);
        
        logger.info("Submission {} evaluation completed: {}/{} test cases passed, score: {}%", 
                   submission.getId(), passedTests, testCases.size(), submission.getScore());
    }

    /**
     * Evaluates a single test case.
     * 
     * @param submission The submission
     * @param testCase The test case
     * @param outputLog The output log
     * @return true if test case passed
     */
    private boolean evaluateTestCase(Submission submission, TestCase testCase, StringBuilder outputLog) {
        // Create Judge0 request
        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode(submission.getSourceCode())
                .languageId(submission.getLanguageId())
                .stdin(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .build();

        // Submit to Judge0
        Judge0SubmissionResponseDto response = judge0Service.submitCode(request);
        
        // Poll for result
        Judge0StatusResponseDto status = pollForResult(response.getToken());
        
        // Update submission with Judge0 results if this is the first test case
        if (submission.getJudge0Token() == null) {
            submission.setJudge0Token(response.getToken());
            updateSubmissionFromJudge0Status(submission, status);
        }

        // Check if test case passed
        boolean passed = status.getStatus() != null && 
                        status.getStatus().getId() == 3 && // Accepted
                        testCase.getExpectedOutput().trim().equals(
                            status.getStdout() != null ? status.getStdout().trim() : "");

        // Log result
        outputLog.append("Test Case ").append(testCase.getId()).append(": ")
                 .append(passed ? "PASSED" : "FAILED").append("\n");
        
        if (!passed && status.getStderr() != null) {
            outputLog.append("Error: ").append(status.getStderr()).append("\n");
        }

        return passed;
    }

    /**
     * Polls Judge0 for result until completion.
     * 
     * @param token The Judge0 token
     * @return The final status
     */
    private Judge0StatusResponseDto pollForResult(String token) {
        int maxAttempts = 30; // 30 seconds max
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            try {
                Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(token, false);
                
                if (status.getStatus() != null && status.getStatus().getId() > 2) {
                    // Finished (status > 2 means not in queue or processing)
                    return status;
                }
                
                Thread.sleep(1000); // Wait 1 second
                attempt++;
                
            } catch (Exception e) {
                logger.error("Error polling Judge0 status: {}", e.getMessage());
                break;
            }
        }
        
        throw new RuntimeException("Timeout waiting for Judge0 result");
    }

    /**
     * Updates submission from Judge0 status response.
     * 
     * @param submission The submission to update
     * @param status The Judge0 status response
     */
    private void updateSubmissionFromJudge0Status(Submission submission, Judge0StatusResponseDto status) {
        if (status.getStatus() != null) {
            submission.setStatus(SubmissionStatus.fromJudge0StatusId(status.getStatus().getId()));
        }
        
        submission.setStderr(status.getStderr());
        submission.setCompileOutput(status.getCompileOutput());
        submission.setMessage(status.getMessage());
        submission.setExitCode(status.getExitCode());
        submission.setTime(status.getTime());
        submission.setWallTime(status.getWallTime());
        submission.setMemory(status.getMemory());
        
        if (submission.getStatus() != null && !submission.getStatus().isProcessing()) {
            submission.setFinishedAt(LocalDateTime.now());
        }
    }

    /**
     * Validates that the language is allowed for the question.
     * 
     * @param question The question
     * @param languageId The language ID
     */
    private void validateLanguage(Question question, Integer languageId) {
        // For now, allow all languages. In the future, check question.getAllowedLanguages()
        // if (question.getAllowedLanguages() != null && 
        //     !question.getAllowedLanguages().contains(languageId)) {
        //     throw new IllegalArgumentException("Language not allowed for this question");
        // }
    }

    /**
     * Gets language name from language ID.
     * This should be replaced with a proper language service in the future.
     * 
     * @param languageId The language ID
     * @return The language name
     */
    private String getLanguageName(Integer languageId) {
        // Common Judge0 language mappings
        return switch (languageId) {
            case 50 -> "C (GCC 9.2.0)";
            case 54 -> "C++ (GCC 9.2.0)";
            case 62 -> "Java (OpenJDK 13.0.1)";
            case 71 -> "Python (3.8.1)";
            case 63 -> "JavaScript (Node.js 12.14.0)";
            default -> "Language " + languageId;
        };
    }
}