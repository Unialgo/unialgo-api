package com.ua.unialgo.judge0.service;

import com.ua.unialgo.judge0.config.Judge0Config;
import com.ua.unialgo.judge0.constants.Judge0Constants;
import com.ua.unialgo.judge0.dto.Judge0StatusResponseDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionRequestDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionResponseDto;
import com.ua.unialgo.judge0.exception.Judge0Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Judge0Service.
 * Mocks Judge0 API responses to test service methods.
 */
@ExtendWith(MockitoExtension.class)
class Judge0ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Judge0Config judge0Config;

    @InjectMocks
    private Judge0Service judge0Service;

    private Judge0SubmissionRequestDto validRequest;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        // Setup valid request
        validRequest = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('Hello, World!')")
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .stdin("test input")
                .cpuTimeLimit(2.0f)
                .memoryLimit(128000)
                .base64Encoded(false)
                .build();

        // Setup headers
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        // Setup config mocks
        when(judge0Config.isAuthenticationEnabled()).thenReturn(false);
    }

    @Test
    void testSubmitCode_Success() {
        // Given
        String expectedToken = "d85cd024-1548-4165-96c7-7bc88673f194";
        Judge0SubmissionResponseDto expectedResponse = new Judge0SubmissionResponseDto(expectedToken);
        ResponseEntity<Judge0SubmissionResponseDto> responseEntity = 
                new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        )).thenReturn(responseEntity);

        // When
        Judge0SubmissionResponseDto result = judge0Service.submitCode(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        );
    }

    @Test
    void testSubmitCode_WithAuthentication() {
        // Given
        when(judge0Config.isAuthenticationEnabled()).thenReturn(true);
        when(judge0Config.getJudge0ApiKey()).thenReturn("test-api-key");

        String expectedToken = "test-token";
        Judge0SubmissionResponseDto expectedResponse = new Judge0SubmissionResponseDto(expectedToken);
        ResponseEntity<Judge0SubmissionResponseDto> responseEntity = 
                new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        )).thenReturn(responseEntity);

        // When
        Judge0SubmissionResponseDto result = judge0Service.submitCode(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
    }

    @Test
    void testSubmitCode_InvalidRequest_NullSourceCode() {
        // Given
        Judge0SubmissionRequestDto invalidRequest = Judge0SubmissionRequestDto.builder()
                .languageId(71)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.submitCode(invalidRequest));
    }

    @Test
    void testSubmitCode_InvalidRequest_NullLanguageId() {
        // Given
        Judge0SubmissionRequestDto invalidRequest = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('test')")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.submitCode(invalidRequest));
    }

    @Test
    void testSubmitCode_HttpClientError() {
        // Given
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, 
                "Bad Request",
                "{\"error\": \"Invalid language_id\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        )).thenThrow(exception);

        // When & Then
        Judge0Exception thrown = assertThrows(Judge0Exception.class, 
                () -> judge0Service.submitCode(validRequest));
        assertTrue(thrown.getMessage().contains("Failed to submit code"));
    }

    @Test
    void testSubmitCode_HttpServerError() {
        // Given
        HttpServerErrorException exception = new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        )).thenThrow(exception);

        // When & Then
        Judge0Exception thrown = assertThrows(Judge0Exception.class, 
                () -> judge0Service.submitCode(validRequest));
        assertTrue(thrown.getMessage().contains("Judge0 server error"));
    }

    @Test
    void testSubmitCode_ConnectionError() {
        // Given
        ResourceAccessException exception = new ResourceAccessException(
                "Connection timeout"
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Judge0SubmissionResponseDto.class)
        )).thenThrow(exception);

        // When & Then
        Judge0Exception thrown = assertThrows(Judge0Exception.class, 
                () -> judge0Service.submitCode(validRequest));
        assertTrue(thrown.getMessage().contains("Unable to connect"));
    }

    @Test
    void testGetSubmissionStatus_Success() {
        // Given
        String token = "test-token";
        Judge0StatusResponseDto expectedStatus = new Judge0StatusResponseDto();
        expectedStatus.setStdout("Hello, World!");
        expectedStatus.setStderr("");
        expectedStatus.setTime("0.001");
        expectedStatus.setMemory(376);
        
        Judge0StatusResponseDto.StatusInfo statusInfo = 
                new Judge0StatusResponseDto.StatusInfo(3, "Accepted");
        expectedStatus.setStatus(statusInfo);

        ResponseEntity<Judge0StatusResponseDto> responseEntity = 
                new ResponseEntity<>(expectedStatus, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Judge0StatusResponseDto.class)
        )).thenReturn(responseEntity);

        // When
        Judge0StatusResponseDto result = judge0Service.getSubmissionStatus(token, false);

        // Then
        assertNotNull(result);
        assertEquals("Hello, World!", result.getStdout());
        assertTrue(result.isAccepted());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Judge0StatusResponseDto.class)
        );
    }

    @Test
    void testGetSubmissionStatus_InvalidToken() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.getSubmissionStatus(null, false));
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.getSubmissionStatus("", false));
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.getSubmissionStatus("  ", false));
    }

    @Test
    void testGetSubmissionStatus_NotFound() {
        // Given
        String token = "non-existent-token";
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.NOT_FOUND
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Judge0StatusResponseDto.class)
        )).thenThrow(exception);

        // When & Then
        Judge0Exception thrown = assertThrows(Judge0Exception.class, 
                () -> judge0Service.getSubmissionStatus(token, false));
        assertTrue(thrown.getMessage().contains("Submission not found"));
    }

    @Test
    void testDeleteSubmission_Success() {
        // Given
        String token = "test-token";
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(responseEntity);

        // When
        assertDoesNotThrow(() -> judge0Service.deleteSubmission(token));

        // Then
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }

    @Test
    void testDeleteSubmission_NotFound() {
        // Given
        String token = "non-existent-token";
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.NOT_FOUND
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenThrow(exception);

        // When & Then - Should not throw exception for NOT_FOUND
        assertDoesNotThrow(() -> judge0Service.deleteSubmission(token));
    }

    @Test
    void testIsAvailable_Success() {
        // Given
        Map<String, Object> aboutResponse = new HashMap<>();
        aboutResponse.put("version", "1.13.0");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(aboutResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("/about"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        boolean result = judge0Service.isAvailable();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsAvailable_Failure() {
        // Given
        when(restTemplate.exchange(
                eq("/about"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection failed"));

        // When
        boolean result = judge0Service.isAvailable();

        // Then
        assertFalse(result);
    }

    @Test
    void testSubmitCode_ExceedsCpuTimeLimit() {
        // Given
        Judge0SubmissionRequestDto invalidRequest = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('test')")
                .languageId(71)
                .cpuTimeLimit(100.0f) // Way over limit
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.submitCode(invalidRequest));
    }

    @Test
    void testSubmitCode_ExceedsMemoryLimit() {
        // Given
        Judge0SubmissionRequestDto invalidRequest = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('test')")
                .languageId(71)
                .memoryLimit(1000000) // Way over limit
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> judge0Service.submitCode(invalidRequest));
    }
}