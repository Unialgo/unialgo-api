package com.ua.unialgo.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for creating a submission request to Judge0 API.
 * Contains all the parameters that can be sent when submitting code for execution.
 */
public class Judge0SubmissionRequestDto {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("cpu_time_limit")
    private Float cpuTimeLimit;

    @JsonProperty("cpu_extra_time")
    private Float cpuExtraTime;

    @JsonProperty("wall_time_limit")
    private Float wallTimeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;

    @JsonProperty("stack_limit")
    private Integer stackLimit;

    @JsonProperty("max_processes_and_or_threads")
    private Integer maxProcessesAndOrThreads;

    @JsonProperty("enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit;

    @JsonProperty("enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    @JsonProperty("max_file_size")
    private Integer maxFileSize;

    @JsonProperty("compiler_options")
    private String compilerOptions;

    @JsonProperty("command_line_arguments")
    private String commandLineArguments;

    @JsonProperty("number_of_runs")
    private Integer numberOfRuns;

    @JsonProperty("base64_encoded")
    private Boolean base64Encoded = false;

    // Constructors
    public Judge0SubmissionRequestDto() {
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
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

    public Float getCpuTimeLimit() {
        return cpuTimeLimit;
    }

    public void setCpuTimeLimit(Float cpuTimeLimit) {
        this.cpuTimeLimit = cpuTimeLimit;
    }

    public Float getCpuExtraTime() {
        return cpuExtraTime;
    }

    public void setCpuExtraTime(Float cpuExtraTime) {
        this.cpuExtraTime = cpuExtraTime;
    }

    public Float getWallTimeLimit() {
        return wallTimeLimit;
    }

    public void setWallTimeLimit(Float wallTimeLimit) {
        this.wallTimeLimit = wallTimeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Integer getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(Integer stackLimit) {
        this.stackLimit = stackLimit;
    }

    public Integer getMaxProcessesAndOrThreads() {
        return maxProcessesAndOrThreads;
    }

    public void setMaxProcessesAndOrThreads(Integer maxProcessesAndOrThreads) {
        this.maxProcessesAndOrThreads = maxProcessesAndOrThreads;
    }

    public Boolean getEnablePerProcessAndThreadTimeLimit() {
        return enablePerProcessAndThreadTimeLimit;
    }

    public void setEnablePerProcessAndThreadTimeLimit(Boolean enablePerProcessAndThreadTimeLimit) {
        this.enablePerProcessAndThreadTimeLimit = enablePerProcessAndThreadTimeLimit;
    }

    public Boolean getEnablePerProcessAndThreadMemoryLimit() {
        return enablePerProcessAndThreadMemoryLimit;
    }

    public void setEnablePerProcessAndThreadMemoryLimit(Boolean enablePerProcessAndThreadMemoryLimit) {
        this.enablePerProcessAndThreadMemoryLimit = enablePerProcessAndThreadMemoryLimit;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getCompilerOptions() {
        return compilerOptions;
    }

    public void setCompilerOptions(String compilerOptions) {
        this.compilerOptions = compilerOptions;
    }

    public String getCommandLineArguments() {
        return commandLineArguments;
    }

    public void setCommandLineArguments(String commandLineArguments) {
        this.commandLineArguments = commandLineArguments;
    }

    public Integer getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(Integer numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public Boolean getBase64Encoded() {
        return base64Encoded;
    }

    public void setBase64Encoded(Boolean base64Encoded) {
        this.base64Encoded = base64Encoded;
    }

    // Builder class
    public static class Builder {
        private final Judge0SubmissionRequestDto dto = new Judge0SubmissionRequestDto();

        public Builder sourceCode(String sourceCode) {
            dto.setSourceCode(sourceCode);
            return this;
        }

        public Builder languageId(Integer languageId) {
            dto.setLanguageId(languageId);
            return this;
        }

        public Builder stdin(String stdin) {
            dto.setStdin(stdin);
            return this;
        }

        public Builder expectedOutput(String expectedOutput) {
            dto.setExpectedOutput(expectedOutput);
            return this;
        }

        public Builder cpuTimeLimit(Float cpuTimeLimit) {
            dto.setCpuTimeLimit(cpuTimeLimit);
            return this;
        }

        public Builder memoryLimit(Integer memoryLimit) {
            dto.setMemoryLimit(memoryLimit);
            return this;
        }

        public Builder base64Encoded(Boolean base64Encoded) {
            dto.setBase64Encoded(base64Encoded);
            return this;
        }

        public Judge0SubmissionRequestDto build() {
            return dto;
        }
    }
}