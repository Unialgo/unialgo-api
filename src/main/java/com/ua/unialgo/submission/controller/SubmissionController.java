package com.ua.unialgo.submission.controller;

import com.ua.unialgo.submission.dto.SubmitCodeRequestDto;
import com.ua.unialgo.submission.dto.SubmissionResponseDto;
import com.ua.unialgo.submission.entity.Submission;
import com.ua.unialgo.submission.service.SubmissionService;
import com.ua.unialgo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

/**
 * REST controller for handling code submissions.
 * Supports both file uploads and direct code input.
 * Follows the existing controller patterns in the codebase.
 */
@RestController
@RequestMapping("/submissions")
@Tag(name = "Submissions", description = "Code submission management")
@SecurityRequirement(name = "OAuth2")
public class SubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;
    private final UserService userService;

    public SubmissionController(SubmissionService submissionService, UserService userService) {
        this.submissionService = submissionService;
        this.userService = userService;
    }

    /**
     * Submits code for evaluation via JSON request.
     * 
     * @param request The submission request
     * @param principal The authenticated user
     * @return The created submission
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit code for evaluation", 
               description = "Submit source code for evaluation against test cases")
    @ApiResponse(responseCode = "201", description = "Submission created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Question not found")
    public ResponseEntity<SubmissionResponseDto> submitCode(
            @Valid @RequestBody SubmitCodeRequestDto request,
            Principal principal) {
        
        logger.info("Received code submission request for question {} from user {}", 
                   request.getQuestionId(), principal.getName());

        Long userId = userService.getCurrentUserId(principal);
        Submission submission = submissionService.submitCode(request, userId);
        SubmissionResponseDto response = submissionService.toDto(submission, false);

        logger.info("Created submission {} for user {}", submission.getId(), userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Submits code via file upload.
     * 
     * @param questionId The question ID
     * @param languageId The language ID
     * @param file The source code file
     * @param principal The authenticated user
     * @return The created submission
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit code via file upload", 
               description = "Submit source code file for evaluation")
    @ApiResponse(responseCode = "201", description = "Submission created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file or request data")
    public ResponseEntity<SubmissionResponseDto> submitCodeFile(
            @RequestParam("questionId") Long questionId,
            @RequestParam("languageId") Integer languageId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        
        logger.info("Received file submission for question {} from user {}", 
                   questionId, principal.getName());

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Read file content
            String sourceCode = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Create request DTO
            SubmitCodeRequestDto request = new SubmitCodeRequestDto(questionId, sourceCode, languageId);
            request.setFileName(file.getOriginalFilename());
            request.setMimeType(file.getContentType());

            // Process submission
            Long userId = userService.getCurrentUserId(principal);
            Submission submission = submissionService.submitCode(request, userId);
            SubmissionResponseDto response = submissionService.toDto(submission, false);

            logger.info("Created submission {} from file upload for user {}", submission.getId(), userId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            logger.error("Error reading uploaded file: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets submission by ID.
     * 
     * @param submissionId The submission ID
     * @param includeSourceCode Whether to include source code
     * @param principal The authenticated user
     * @return The submission details
     */
    @GetMapping("/{submissionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Get submission by ID", 
               description = "Retrieve detailed information about a specific submission")
    @ApiResponse(responseCode = "200", description = "Submission found")
    @ApiResponse(responseCode = "404", description = "Submission not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<SubmissionResponseDto> getSubmission(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "false") boolean includeSourceCode,
            Principal principal) {
        
        Submission submission = submissionService.findById(submissionId);
        Long userId = userService.getCurrentUserId(principal);

        // Check access permissions
        if (!canAccessSubmission(submission, userId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SubmissionResponseDto response = submissionService.toDto(submission, includeSourceCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets submissions by user (current user or specified user for teachers).
     * 
     * @param userId The user ID (optional, defaults to current user)
     * @param page The page number
     * @param size The page size
     * @param sortBy The sort field
     * @param sortDirection The sort direction
     * @param principal The authenticated user
     * @return Page of submissions
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Get user submissions", 
               description = "Retrieve submissions for the current user or specified user (teachers only)")
    @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    public ResponseEntity<Page<SubmissionResponseDto>> getUserSubmissions(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Principal principal) {
        
        Long currentUserId = userService.getCurrentUserId(principal);
        
        // If no userId specified, use current user
        if (userId == null) {
            userId = currentUserId;
        }
        
        // Check access permissions
        if (!canAccessUserData(userId, currentUserId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Submission> submissions = submissionService.findByUserId(userId, pageable);
        Page<SubmissionResponseDto> response = submissions.map(s -> submissionService.toDto(s, false));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets submissions by question.
     * 
     * @param questionId The question ID
     * @param page The page number
     * @param size The page size
     * @param principal The authenticated user
     * @return Page of submissions
     */
    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get question submissions", 
               description = "Retrieve all submissions for a specific question (teachers only)")
    @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    public ResponseEntity<Page<SubmissionResponseDto>> getQuestionSubmissions(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        
        Sort sort = Sort.by(Sort.Direction.DESC, "submissionDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Submission> submissions = submissionService.findByQuestionId(questionId, pageable);
        Page<SubmissionResponseDto> response = submissions.map(s -> submissionService.toDto(s, false));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets submissions by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @param page The page number
     * @param size The page size
     * @param principal The authenticated user
     * @return Page of submissions
     */
    @GetMapping("/user/{userId}/question/{questionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Get user submissions for question", 
               description = "Retrieve submissions for a specific user and question")
    @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    public ResponseEntity<Page<SubmissionResponseDto>> getUserQuestionSubmissions(
            @PathVariable Long userId,
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        
        Long currentUserId = userService.getCurrentUserId(principal);
        
        // Check access permissions
        if (!canAccessUserData(userId, currentUserId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "submissionDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Submission> submissions = submissionService.findByUserAndQuestion(userId, questionId, pageable);
        Page<SubmissionResponseDto> response = submissions.map(s -> submissionService.toDto(s, true));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the latest submission for user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @param principal The authenticated user
     * @return The latest submission or 404
     */
    @GetMapping("/user/{userId}/question/{questionId}/latest")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Get latest submission", 
               description = "Retrieve the most recent submission for a user and question")
    @ApiResponse(responseCode = "200", description = "Latest submission found")
    @ApiResponse(responseCode = "404", description = "No submissions found")
    public ResponseEntity<SubmissionResponseDto> getLatestSubmission(
            @PathVariable Long userId,
            @PathVariable Long questionId,
            Principal principal) {
        
        Long currentUserId = userService.getCurrentUserId(principal);
        
        // Check access permissions
        if (!canAccessUserData(userId, currentUserId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Submission submission = submissionService.getLatestSubmission(userId, questionId);
        
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }

        SubmissionResponseDto response = submissionService.toDto(submission, true);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the best submission for user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @param principal The authenticated user
     * @return The best submission or 404
     */
    @GetMapping("/user/{userId}/question/{questionId}/best")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Get best submission", 
               description = "Retrieve the highest scoring submission for a user and question")
    @ApiResponse(responseCode = "200", description = "Best submission found")
    @ApiResponse(responseCode = "404", description = "No submissions found")
    public ResponseEntity<SubmissionResponseDto> getBestSubmission(
            @PathVariable Long userId,
            @PathVariable Long questionId,
            Principal principal) {
        
        Long currentUserId = userService.getCurrentUserId(principal);
        
        // Check access permissions
        if (!canAccessUserData(userId, currentUserId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Submission submission = submissionService.getBestSubmission(userId, questionId);
        
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }

        SubmissionResponseDto response = submissionService.toDto(submission, true);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates submission status (for polling/refresh).
     * 
     * @param submissionId The submission ID
     * @param principal The authenticated user
     * @return Updated submission
     */
    @PutMapping("/{submissionId}/status")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    @Operation(summary = "Update submission status", 
               description = "Force update of submission status from Judge0")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "404", description = "Submission not found")
    public ResponseEntity<SubmissionResponseDto> updateSubmissionStatus(
            @PathVariable Long submissionId,
            Principal principal) {
        
        Submission submission = submissionService.findById(submissionId);
        Long userId = userService.getCurrentUserId(principal);

        // Check access permissions
        if (!canAccessSubmission(submission, userId, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Submission updatedSubmission = submissionService.updateSubmissionStatus(submissionId);
        SubmissionResponseDto response = submissionService.toDto(updatedSubmission, false);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if user can access a specific submission.
     * 
     * @param submission The submission
     * @param currentUserId The current user ID
     * @param principal The authenticated user
     * @return true if access is allowed
     */
    private boolean canAccessSubmission(Submission submission, Long currentUserId, Principal principal) {
        // Users can access their own submissions
        if (submission.getUser().getId().equals(currentUserId)) {
            return true;
        }
        
        // Teachers can access all submissions
        return userService.hasRole(principal, "TEACHER");
    }

    /**
     * Checks if user can access data for a specific user.
     * 
     * @param targetUserId The target user ID
     * @param currentUserId The current user ID
     * @param principal The authenticated user
     * @return true if access is allowed
     */
    private boolean canAccessUserData(Long targetUserId, Long currentUserId, Principal principal) {
        // Users can access their own data
        if (targetUserId.equals(currentUserId)) {
            return true;
        }
        
        // Teachers can access all user data
        return userService.hasRole(principal, "TEACHER");
    }
}