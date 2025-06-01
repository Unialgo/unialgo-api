package com.ua.unialgo.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for the response received after creating a submission in Judge0.
 * Contains the token which can be used to check the submission status.
 */
public class Judge0SubmissionResponseDto {

    @JsonProperty("token")
    private String token;

    // Constructors
    public Judge0SubmissionResponseDto() {
    }

    public Judge0SubmissionResponseDto(String token) {
        this.token = token;
    }

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Judge0SubmissionResponseDto{" +
                "token='" + token + '\'' +
                '}';
    }
}