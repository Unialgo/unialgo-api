package com.ua.unialgo.submission.repository;

import com.ua.unialgo.submission.entity.Submission;
import com.ua.unialgo.submission.entity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Submission entity.
 * Follows the existing repository patterns in the codebase.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Finds all submissions by user ID.
     * 
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    Page<Submission> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds all submissions by question ID.
     * 
     * @param questionId The question ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    Page<Submission> findByQuestionId(Long questionId, Pageable pageable);

    /**
     * Finds all submissions by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @param pageable Pagination information
     * @return Page of submissions
     */
    Page<Submission> findByUserIdAndQuestionId(Long userId, Long questionId, Pageable pageable);

    /**
     * Finds submissions by status.
     * 
     * @param status The submission status
     * @return List of submissions with the specified status
     */
    List<Submission> findByStatus(SubmissionStatus status);

    /**
     * Finds submissions that are still processing (pending or in queue).
     * 
     * @return List of processing submissions
     */
    @Query("SELECT s FROM Submission s WHERE s.status IN (com.ua.unialgo.submission.entity.SubmissionStatus.PENDING, com.ua.unialgo.submission.entity.SubmissionStatus.IN_QUEUE, com.ua.unialgo.submission.entity.SubmissionStatus.PROCESSING, com.ua.unialgo.submission.entity.SubmissionStatus.EVALUATING)")
    List<Submission> findProcessingSubmissions();

    /**
     * Finds submission by Judge0 token.
     * 
     * @param judge0Token The Judge0 token
     * @return Optional submission
     */
    Optional<Submission> findByJudge0Token(String judge0Token);

    /**
     * Finds the latest submission by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return Optional latest submission
     */
    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.question.id = :questionId ORDER BY s.submissionDate DESC LIMIT 1")
    Optional<Submission> findLatestSubmissionByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * Finds the best submission (highest score) by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return Optional best submission
     */
    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.question.id = :questionId ORDER BY s.score DESC, s.submissionDate DESC LIMIT 1")
    Optional<Submission> findBestSubmissionByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * Counts submissions by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return Number of submissions
     */
    long countByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * Counts successful submissions by user and question.
     * 
     * @param userId The user ID
     * @param questionId The question ID
     * @return Number of accepted submissions
     */
    long countByUserIdAndQuestionIdAndStatus(Long userId, Long questionId, SubmissionStatus status);

    /**
     * Finds submissions created after a specific date.
     * 
     * @param date The date threshold
     * @return List of recent submissions
     */
    List<Submission> findBySubmissionDateAfter(LocalDateTime date);

    /**
     * Finds submissions that need status updates (have Judge0 token but are still processing).
     * 
     * @return List of submissions to check
     */
    @Query("SELECT s FROM Submission s WHERE s.judge0Token IS NOT NULL AND s.status IN (com.ua.unialgo.submission.entity.SubmissionStatus.IN_QUEUE, com.ua.unialgo.submission.entity.SubmissionStatus.PROCESSING, com.ua.unialgo.submission.entity.SubmissionStatus.EVALUATING)")
    List<Submission> findSubmissionsNeedingStatusUpdate();

    /**
     * Finds all submissions by user ID ordered by submission date descending.
     * 
     * @param userId The user ID
     * @return List of submissions
     */
    List<Submission> findByUserIdOrderBySubmissionDateDesc(Long userId);

    /**
     * Finds all submissions by question ID ordered by score descending.
     * 
     * @param questionId The question ID
     * @return List of submissions ordered by score
     */
    List<Submission> findByQuestionIdOrderByScoreDescSubmissionDateDesc(Long questionId);

    /**
     * Deletes old submissions older than specified date.
     * Used for cleanup operations.
     * 
     * @param date The date threshold
     */
    void deleteBySubmissionDateBefore(LocalDateTime date);
}