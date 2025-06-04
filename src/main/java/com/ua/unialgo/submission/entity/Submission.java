package com.ua.unialgo.submission.entity;

import com.ua.unialgo.question.entity.Question;
import com.ua.unialgo.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a code submission by a student.
 * Follows the existing entity patterns in the codebase.
 */
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "source_code", columnDefinition = "TEXT", nullable = false)
    private String sourceCode;

    @Column(name = "language_id", nullable = false)
    private Integer languageId;

    @Column(name = "judge0_token", length = 50)
    private String judge0Token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(name = "stdout", columnDefinition = "TEXT")
    private String stdout;

    @Column(name = "stderr", columnDefinition = "TEXT")
    private String stderr;

    @Column(name = "compile_output", columnDefinition = "TEXT")
    private String compileOutput;

    @Column(name = "message")
    private String message;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "exit_signal")
    private Integer exitSignal;

    @Column(name = "time")
    private Float time;

    @Column(name = "wall_time")
    private Float wallTime;

    @Column(name = "memory")
    private Integer memory;

    @Column(name = "test_cases_passed")
    private Integer testCasesPassed = 0;

    @Column(name = "total_test_cases")
    private Integer totalTestCases = 0;

    @Column(name = "score")
    private Float score = 0.0f;

    @CreationTimestamp
    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    // Constructors
    public Submission() {
    }

    public Submission(User user, Question question, String sourceCode, Integer languageId) {
        this.user = user;
        this.question = question;
        this.sourceCode = sourceCode;
        this.languageId = languageId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getJudge0Token() {
        return judge0Token;
    }

    public void setJudge0Token(String judge0Token) {
        this.judge0Token = judge0Token;
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

    public Integer getExitSignal() {
        return exitSignal;
    }

    public void setExitSignal(Integer exitSignal) {
        this.exitSignal = exitSignal;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    // Utility methods
    public boolean isFinished() {
        return status == SubmissionStatus.ACCEPTED || 
               status == SubmissionStatus.WRONG_ANSWER ||
               status == SubmissionStatus.TIME_LIMIT_EXCEEDED ||
               status == SubmissionStatus.MEMORY_LIMIT_EXCEEDED ||
               status == SubmissionStatus.RUNTIME_ERROR ||
               status == SubmissionStatus.COMPILATION_ERROR ||
               status == SubmissionStatus.INTERNAL_ERROR;
    }

    public boolean isSuccessful() {
        return status == SubmissionStatus.ACCEPTED;
    }

    public void calculateScore() {
        if (totalTestCases != null && totalTestCases > 0) {
            this.score = (float) testCasesPassed / totalTestCases * 100.0f;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", status=" + status +
                ", languageId=" + languageId +
                ", score=" + score +
                ", submissionDate=" + submissionDate +
                '}';
    }
}