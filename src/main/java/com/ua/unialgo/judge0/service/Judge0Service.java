package com.ua.unialgo.judge0.service;

import com.ua.unialgo.judge0.config.Judge0Config;
import com.ua.unialgo.judge0.constants.Judge0Constants;
import com.ua.unialgo.judge0.dto.Judge0StatusResponseDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionRequestDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionResponseDto;
import com.ua.unialgo.judge0.exception.Judge0Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

/**
 * Service responsible for interacting with Judge0 API.
 * Handles code submission and status checking.
 */
@Service
public class Judge0Service {

    private static final Logger logger = LoggerFactory.getLogger(Judge0Service.class);

    private final RestTemplate restTemplate;
    private final Judge0Config judge0Config;

    public Judge0Service(@Qualifier("judge0RestTemplate") RestTemplate restTemplate, 
                        Judge0Config judge0Config) {
        this.restTemplate = restTemplate;
        this.judge0Config = judge0Config;
    }

    /**
     * Submits code to Judge0 for execution.
     * Subtask 2: Implement submitCode Method
     * 
     * @param request The submission request containing code and parameters
     * @return The submission response containing the token
     * @throws Judge0Exception if submission fails
     */
    public Judge0SubmissionResponseDto submitCode(Judge0SubmissionRequestDto request) {
        logger.debug("Submitting code to Judge0 - Language ID: {}", request.getLanguageId());
        
        try {
            // Validate request
            validateSubmissionRequest(request);
            
            // Build headers
            HttpHeaders headers = buildHeaders();
            
            // Create HTTP entity
            HttpEntity<Judge0SubmissionRequestDto> entity = new HttpEntity<>(request, headers);
            
            // Build submission URL with query parameters
            String submissionUrl = UriComponentsBuilder.fromPath("/submissions")
                    .queryParam("base64_encoded", request.getBase64Encoded())
                    .queryParam("wait", false) // We'll poll for status separately
                    .build()
                    .toUriString();
            
            // Send request
            ResponseEntity<Judge0SubmissionResponseDto> response = restTemplate.exchange(
                    submissionUrl,
                    HttpMethod.POST,
                    entity,
                    Judge0SubmissionResponseDto.class
            );
            
            // Validate response
            if (response.getBody() == null || response.getBody().getToken() == null) {
                throw new Judge0Exception("Invalid response from Judge0: missing token");
            }
            
            logger.info("Successfully submitted code to Judge0. Token: {}", 
                    response.getBody().getToken());
            
            return response.getBody();
            
        } catch (HttpClientErrorException e) {
            // Handle 4xx errors
            logger.error("Client error submitting to Judge0: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new Judge0Exception(
                    "Failed to submit code: " + extractErrorMessage(e), e);
                    
        } catch (HttpServerErrorException e) {
            // Handle 5xx errors
            logger.error("Server error submitting to Judge0: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new Judge0Exception(
                    "Judge0 server error: " + e.getStatusCode(), e);
                    
        } catch (ResourceAccessException e) {
            // Handle connection/timeout errors
            logger.error("Connection error with Judge0: {}", e.getMessage());
            throw new Judge0Exception(
                    "Unable to connect to Judge0 API", e);
                    
        } catch (Exception e) {
            logger.error("Unexpected error submitting to Judge0", e);
            throw new Judge0Exception(
                    "Unexpected error during submission", e);
        }
    }

    /**
     * Retrieves the status and results of a submission.
     * Subtask 3: Implement getSubmissionStatus Method
     * 
     * @param token The submission token
     * @param includeSourceCode Whether to include source code in response
     * @return The submission status and results
     * @throws Judge0Exception if status retrieval fails
     */
    public Judge0StatusResponseDto getSubmissionStatus(String token, boolean includeSourceCode) {
        logger.debug("Getting submission status for token: {}", token);
        
        try {
            // Validate token
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
            
            // Build headers
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            // Build status URL with query parameters
            String fields = includeSourceCode ? "*" : 
                    "stdout,stderr,compile_output,message,exit_code,exit_signal," +
                    "status,created_at,finished_at,time,wall_time,memory,language";
            
            String statusUrl = UriComponentsBuilder.fromPath("/submissions/{token}")
                    .queryParam("base64_encoded", false)
                    .queryParam("fields", fields)
                    .buildAndExpand(token)
                    .toUriString();
            
            // Send request
            ResponseEntity<Judge0StatusResponseDto> response = restTemplate.exchange(
                    statusUrl,
                    HttpMethod.GET,
                    entity,
                    Judge0StatusResponseDto.class
            );
            
            // Validate response
            if (response.getBody() == null) {
                throw new Judge0Exception("Invalid response from Judge0: missing status data");
            }
            
            Judge0StatusResponseDto status = response.getBody();
            logger.info("Retrieved status for token {}: {}", 
                    token, status.getStatus() != null ? status.getStatus().getDescription() : "Unknown");
            
            return status;
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("Submission not found for token: {}", token);
                throw new Judge0Exception("Submission not found", e);
            }
            logger.error("Client error getting status from Judge0: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new Judge0Exception(
                    "Failed to get submission status: " + extractErrorMessage(e), e);
                    
        } catch (HttpServerErrorException e) {
            logger.error("Server error getting status from Judge0: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new Judge0Exception(
                    "Judge0 server error: " + e.getStatusCode(), e);
                    
        } catch (ResourceAccessException e) {
            logger.error("Connection error with Judge0: {}", e.getMessage());
            throw new Judge0Exception(
                    "Unable to connect to Judge0 API", e);
                    
        } catch (Exception e) {
            logger.error("Unexpected error getting status from Judge0", e);
            throw new Judge0Exception(
                    "Unexpected error retrieving status", e);
        }
    }

    /**
     * Deletes a submission from Judge0.
     * 
     * @param token The submission token
     * @throws Judge0Exception if deletion fails
     */
    public void deleteSubmission(String token) {
        logger.debug("Deleting submission with token: {}", token);
        
        try {
            // Build headers
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            // Build delete URL
            String deleteUrl = UriComponentsBuilder.fromPath("/submissions/{token}")
                    .buildAndExpand(token)
                    .toUriString();
            
            // Send delete request
            restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
            
            logger.info("Successfully deleted submission with token: {}", token);
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Submission not found for deletion: {}", token);
                return; // Don't throw exception if already deleted
            }
            logger.error("Error deleting submission: {}", e.getMessage());
            throw new Judge0Exception("Failed to delete submission", e);
        }
    }

    /**
     * Checks if Judge0 API is available.
     * 
     * @return true if API is reachable, false otherwise
     */
    public boolean isAvailable() {
        try {
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/about",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Judge0 API is not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates submission request.
     * Subtask 4: Handle API Responses and Implement Error Handling
     */
    private void validateSubmissionRequest(Judge0SubmissionRequestDto request) {
        if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Source code cannot be null or empty");
        }
        
        if (request.getLanguageId() == null) {
            throw new IllegalArgumentException("Language ID cannot be null");
        }
        
        // Validate limits if provided
        if (request.getCpuTimeLimit() != null && 
            request.getCpuTimeLimit() > Judge0Constants.Defaults.CPU_TIME_LIMIT * 3) {
            throw new IllegalArgumentException(
                    "CPU time limit exceeds maximum allowed value");
        }
        
        if (request.getMemoryLimit() != null && 
            request.getMemoryLimit() > Judge0Constants.Defaults.MEMORY_LIMIT * 4) {
            throw new IllegalArgumentException(
                    "Memory limit exceeds maximum allowed value");
        }
    }

    /**
     * Builds HTTP headers for Judge0 API requests.
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        
        // Add authentication header if API key is configured
        if (judge0Config.isAuthenticationEnabled()) {
            headers.add("X-Auth-Token", judge0Config.getJudge0ApiKey());
        }
        
        return headers;
    }

    /**
     * Extracts error message from HTTP exception.
     * Subtask 4: Handle API Responses and Implement Error Handling
     */
    private String extractErrorMessage(HttpClientErrorException e) {
        try {
            // Try to parse error message from response body
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                // Simple extraction - in production, parse JSON properly
                return e.getStatusCode() + ": " + responseBody;
            }
        } catch (Exception ex) {
            logger.debug("Could not extract error message", ex);
        }
        return e.getStatusCode().toString();
    }
}