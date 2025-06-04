package com.ua.unialgo.submission.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the status of a code submission.
 * Maps to Judge0 status IDs and descriptions.
 */
public enum SubmissionStatus {
    
    // Queue states
    IN_QUEUE(1, "In Queue"),
    PROCESSING(2, "Processing"),
    
    // Accepted
    ACCEPTED(3, "Accepted"),
    
    // Error states
    WRONG_ANSWER(4, "Wrong Answer"),
    TIME_LIMIT_EXCEEDED(5, "Time Limit Exceeded"),
    COMPILATION_ERROR(6, "Compilation Error"),
    RUNTIME_ERROR(7, "Runtime Error"),
    RUNTIME_ERROR_SIGKILL(8, "Runtime Error (SIGKILL)"),
    RUNTIME_ERROR_SIGFPE(9, "Runtime Error (SIGFPE)"),
    RUNTIME_ERROR_SIGSEGV(10, "Runtime Error (SIGSEGV)"),
    RUNTIME_ERROR_SIGXFSZ(11, "Runtime Error (SIGXFSZ)"),
    RUNTIME_ERROR_SIGABRT(12, "Runtime Error (SIGABRT)"),
    RUNTIME_ERROR_NZEC(13, "Runtime Error (NZEC)"),
    RUNTIME_ERROR_OTHER(14, "Runtime Error (Other)"),
    
    // Internal/System errors
    INTERNAL_ERROR(15, "Internal Error"),
    EXEC_FORMAT_ERROR(16, "Exec Format Error"),
    
    // Memory/Output limits
    MEMORY_LIMIT_EXCEEDED(17, "Memory Limit Exceeded"),
    OUTPUT_LIMIT_EXCEEDED(19, "Output Limit Exceeded"),
    
    // Custom states for our application
    PENDING(0, "Pending"),
    EVALUATING(99, "Evaluating");

    private final int id;
    private final String description;

    SubmissionStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    /**
     * Maps Judge0 status ID to our enum.
     * 
     * @param statusId Judge0 status ID
     * @return Corresponding SubmissionStatus
     */
    public static SubmissionStatus fromJudge0StatusId(int statusId) {
        for (SubmissionStatus status : values()) {
            if (status.getId() == statusId) {
                return status;
            }
        }
        return INTERNAL_ERROR; // Default for unknown status
    }

    /**
     * Checks if the status indicates the submission is still processing.
     * 
     * @return true if submission is still being processed
     */
    public boolean isProcessing() {
        return this == IN_QUEUE || this == PROCESSING || this == PENDING || this == EVALUATING;
    }

    /**
     * Checks if the status indicates a successful execution.
     * 
     * @return true if submission was accepted
     */
    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    /**
     * Checks if the status indicates a compilation error.
     * 
     * @return true if compilation failed
     */
    public boolean isCompilationError() {
        return this == COMPILATION_ERROR;
    }

    /**
     * Checks if the status indicates a runtime error.
     * 
     * @return true if runtime error occurred
     */
    public boolean isRuntimeError() {
        return this == RUNTIME_ERROR || 
               this == RUNTIME_ERROR_SIGKILL ||
               this == RUNTIME_ERROR_SIGFPE ||
               this == RUNTIME_ERROR_SIGSEGV ||
               this == RUNTIME_ERROR_SIGXFSZ ||
               this == RUNTIME_ERROR_SIGABRT ||
               this == RUNTIME_ERROR_NZEC ||
               this == RUNTIME_ERROR_OTHER;
    }

    /**
     * Checks if the status indicates a system/internal error.
     * 
     * @return true if system error occurred
     */
    public boolean isSystemError() {
        return this == INTERNAL_ERROR || this == EXEC_FORMAT_ERROR;
    }

    @Override
    public String toString() {
        return description;
    }
}