package com.ua.unialgo.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for code submission requests.
 * Handles file uploads and direct code input for frontend integration.
 */
public class SubmitCodeRequestDto {

    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be positive")
    private Long questionId;

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    @NotNull(message = "Language ID is required")
    @Positive(message = "Language ID must be positive")
    private Integer languageId;

    private String fileName;

    private String mimeType;

    // Constructors
    public SubmitCodeRequestDto() {
    }

    public SubmitCodeRequestDto(Long questionId, String sourceCode, Integer languageId) {
        this.questionId = questionId;
        this.sourceCode = sourceCode;
        this.languageId = languageId;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "SubmitCodeRequestDto{" +
                "questionId=" + questionId +
                ", languageId=" + languageId +
                ", fileName='" + fileName + '\'' +
                ", sourceCodeLength=" + (sourceCode != null ? sourceCode.length() : 0) +
                '}';
    }
}