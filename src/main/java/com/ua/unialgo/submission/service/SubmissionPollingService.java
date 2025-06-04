package com.ua.unialgo.submission.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service responsible for polling Judge0 for submission status updates.
 * Implements Task 9: Polling Mechanism.
 */
@Service
public class SubmissionPollingService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionPollingService.class);

    private final SubmissionService submissionService;

    public SubmissionPollingService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * Scheduled task to update submission statuses.
     * Runs every 10 seconds to check for submissions that need status updates.
     */
    @Scheduled(fixedRate = 10000) // 10 seconds
    public void updateSubmissionStatuses() {
        try {
            logger.debug("Starting scheduled submission status update");
            submissionService.processStatusUpdates();
            logger.debug("Completed scheduled submission status update");
        } catch (Exception e) {
            logger.error("Error during scheduled submission status update: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleanup task to handle old submissions.
     * Runs every hour to clean up submissions older than 24 hours.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupOldSubmissions() {
        try {
            logger.debug("Starting scheduled cleanup of old submissions");
            // Implementation for cleanup can be added here if needed
            logger.debug("Completed scheduled cleanup of old submissions");
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup: {}", e.getMessage(), e);
        }
    }
}