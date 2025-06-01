package com.ua.unialgo.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO for the submission status response from Judge0 API.
 * Contains all the information about the execution result of a submission.
 */
public class Judge0StatusResponseDto {

    @JsonProperty("stdout")
    private String stdout;

    @JsonProperty("stderr")
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    @JsonProperty("message")
    private String message;

    @JsonProperty("exit_code")
    private Integer exitCode;

    @JsonProperty("exit_signal")
    private Integer exitSignal;

    @JsonProperty("status")
    private StatusInfo status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("finished_at")
    private LocalDateTime finishedAt;

    @JsonProperty("token")
    private String token;

    @JsonProperty("time")
    private String time;

    @JsonProperty("wall_time")
    private String wallTime;

    @JsonProperty("memory")
    private Integer memory;

    @JsonProperty("language")
    private LanguageInfo language;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    // Nested class for status information
    public static class StatusInfo {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("description")
        private String description;

        // Constructors
        public StatusInfo() {
        }

        public StatusInfo(Integer id, String description) {
            this.id = id;
            this.description = description;
        }

        // Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "StatusInfo{" +
                    "id=" + id +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    // Nested class for language information
    public static class LanguageInfo {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        // Constructors
        public LanguageInfo() {
        }

        public LanguageInfo(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "LanguageInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    // Constructors
    public Judge0StatusResponseDto() {
    }

    // Getters and Setters
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

    public StatusInfo getStatus() {
        return status;
    }

    public void setStatus(StatusInfo status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWallTime() {
        return wallTime;
    }

    public void setWallTime(String wallTime) {
        this.wallTime = wallTime;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public LanguageInfo getLanguage() {
        return language;
    }

    public void setLanguage(LanguageInfo language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    /**
     * Helper method to check if the submission was accepted (status id = 3)
     */
    public boolean isAccepted() {
        return status != null && status.getId() != null && status.getId() == 3;
    }

    /**
     * Helper method to check if the submission is still processing (status id = 1 or 2)
     */
    public boolean isProcessing() {
        return status != null && status.getId() != null && (status.getId() == 1 || status.getId() == 2);
    }

    /**
     * Helper method to check if there was a compilation error (status id = 6)
     */
    public boolean hasCompilationError() {
        return status != null && status.getId() != null && status.getId() == 6;
    }

    /**
     * Helper method to check if there was a runtime error (status id = 7-12)
     */
    public boolean hasRuntimeError() {
        return status != null && status.getId() != null && status.getId() >= 7 && status.getId() <= 12;
    }

    @Override
    public String toString() {
        return "Judge0StatusResponseDto{" +
                "stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                ", status=" + status +
                ", time='" + time + '\'' +
                ", memory=" + memory +
                ", token='" + token + '\'' +
                '}';
    }
}