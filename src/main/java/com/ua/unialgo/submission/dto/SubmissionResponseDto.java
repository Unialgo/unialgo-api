package com.ua.unialgo.submission.dto;

import com.ua.unialgo.submission.entity.SubmissionStatus;

import java.time.LocalDateTime;

/**
 * DTO for submission responses.
 * Contains all relevant information about a submission for frontend display.
 */
public class SubmissionResponseDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private Long questionId;
    private String questionTitle;
    private Integer languageId;
    private String languageName;
    private SubmissionStatus status;
    private String stdout;
    private String stderr;
    private String compileOutput;
    private String message;
    private Integer exitCode;
    private Float time;
    private Float wallTime;
    private Integer memory;
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private Float score;
    private LocalDateTime submissionDate;
    private LocalDateTime finishedAt;
    private String sourceCode; // Optional, only for detailed view

    // Constructors
    public SubmissionResponseDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public Float getWallTime() {
        return wallTime;
    }

    public void setWallTime(Float wallTime) {
        this.wallTime = wallTime;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getTestCasesPassed() {
        return testCasesPassed;
    }

    public void setTestCasesPassed(Integer testCasesPassed) {
        this.testCasesPassed = testCasesPassed;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(Integer totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // Utility methods
    public boolean isFinished() {
        return status != null && !status.isProcessing();
    }

    public boolean isSuccessful() {
        return status != null && status.isAccepted();
    }

    public String getProgressPercentage() {
        if (totalTestCases == null || totalTestCases == 0) {
            return "0%";
        }
        return String.format("%.1f%%", score);
    }

    @Override
    public String toString() {
        return "SubmissionResponseDto{" +
                "id=" + id +
                ", status=" + status +
                ", score=" + score +
                ", testCasesPassed=" + testCasesPassed +
                ", totalTestCases=" + totalTestCases +
                ", submissionDate=" + submissionDate +
                '}';
    }
}