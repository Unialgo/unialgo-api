package com.ua.unialgo.judge0.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ua.unialgo.judge0.constants.Judge0Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Judge0 DTOs to ensure correct serialization and deserialization.
 */
class Judge0DtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testJudge0SubmissionRequestDto_Serialization() throws Exception {
        // Given
        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('Hello, World!')")
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .stdin("test input")
                .expectedOutput("Hello, World!")
                .cpuTimeLimit(2.0f)
                .memoryLimit(128000)
                .base64Encoded(false)
                .build();

        // When
        String json = objectMapper.writeValueAsString(request);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"source_code\":\"print('Hello, World!')\""));
        assertTrue(json.contains("\"language_id\":71"));
        assertTrue(json.contains("\"stdin\":\"test input\""));
        assertTrue(json.contains("\"expected_output\":\"Hello, World!\""));
        assertTrue(json.contains("\"cpu_time_limit\":2.0"));
        assertTrue(json.contains("\"memory_limit\":128000"));
        assertTrue(json.contains("\"base64_encoded\":false"));
    }

    @Test
    void testJudge0SubmissionRequestDto_Deserialization() throws Exception {
        // Given
        String json = """
                {
                    "source_code": "System.out.println(\\"Hello Java!\\");",
                    "language_id": 62,
                    "stdin": "input data",
                    "cpu_time_limit": 3.0,
                    "memory_limit": 256000,
                    "base64_encoded": true
                }
                """;

        // When
        Judge0SubmissionRequestDto request = objectMapper.readValue(json, Judge0SubmissionRequestDto.class);

        // Then
        assertNotNull(request);
        assertEquals("System.out.println(\"Hello Java!\");", request.getSourceCode());
        assertEquals(62, request.getLanguageId());
        assertEquals("input data", request.getStdin());
        assertEquals(3.0f, request.getCpuTimeLimit());
        assertEquals(256000, request.getMemoryLimit());
        assertTrue(request.getBase64Encoded());
    }

    @Test
    void testJudge0SubmissionResponseDto_Serialization() throws Exception {
        // Given
        Judge0SubmissionResponseDto response = new Judge0SubmissionResponseDto("d85cd024-1548-4165-96c7-7bc88673f194");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"token\":\"d85cd024-1548-4165-96c7-7bc88673f194\""));
    }

    @Test
    void testJudge0SubmissionResponseDto_Deserialization() throws Exception {
        // Given
        String json = """
                {
                    "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                }
                """;

        // When
        Judge0SubmissionResponseDto response = objectMapper.readValue(json, Judge0SubmissionResponseDto.class);

        // Then
        assertNotNull(response);
        assertEquals("a1b2c3d4-e5f6-7890-abcd-ef1234567890", response.getToken());
    }

    @Test
    void testJudge0StatusResponseDto_Serialization() throws Exception {
        // Given
        Judge0StatusResponseDto status = new Judge0StatusResponseDto();
        status.setStdout("Hello, World!\n");
        status.setStderr("");
        status.setCompileOutput(null);
        status.setMessage(null);
        status.setExitCode(0);
        status.setTime("0.001");
        status.setMemory(376);
        status.setToken("test-token");
        
        Judge0StatusResponseDto.StatusInfo statusInfo = new Judge0StatusResponseDto.StatusInfo(
                Judge0Constants.Status.ACCEPTED, "Accepted"
        );
        status.setStatus(statusInfo);
        
        Judge0StatusResponseDto.LanguageInfo languageInfo = new Judge0StatusResponseDto.LanguageInfo(
                71, "Python (3.8.1)"
        );
        status.setLanguage(languageInfo);

        // When
        String json = objectMapper.writeValueAsString(status);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"stdout\":\"Hello, World!\\n\""));
        assertTrue(json.contains("\"stderr\":\"\""));
        assertTrue(json.contains("\"exit_code\":0"));
        assertTrue(json.contains("\"time\":\"0.001\""));
        assertTrue(json.contains("\"memory\":376"));
        assertTrue(json.contains("\"token\":\"test-token\""));
        assertTrue(json.contains("\"id\":3"));
        assertTrue(json.contains("\"description\":\"Accepted\""));
    }

    @Test
    void testJudge0StatusResponseDto_Deserialization() throws Exception {
        // Given
        String json = """
                {
                    "stdout": "42\\n",
                    "stderr": "",
                    "compile_output": null,
                    "message": null,
                    "exit_code": 0,
                    "exit_signal": null,
                    "status": {
                        "id": 3,
                        "description": "Accepted"
                    },
                    "created_at": "2024-01-15T10:30:00",
                    "finished_at": "2024-01-15T10:30:01",
                    "token": "test-token-123",
                    "time": "0.005",
                    "wall_time": "0.010",
                    "memory": 1024,
                    "language": {
                        "id": 71,
                        "name": "Python (3.8.1)"
                    }
                }
                """;

        // When
        Judge0StatusResponseDto status = objectMapper.readValue(json, Judge0StatusResponseDto.class);

        // Then
        assertNotNull(status);
        assertEquals("42\n", status.getStdout());
        assertEquals("", status.getStderr());
        assertNull(status.getCompileOutput());
        assertEquals(0, status.getExitCode());
        assertEquals("test-token-123", status.getToken());
        assertEquals("0.005", status.getTime());
        assertEquals("0.010", status.getWallTime());
        assertEquals(1024, status.getMemory());
        
        assertNotNull(status.getStatus());
        assertEquals(3, status.getStatus().getId());
        assertEquals("Accepted", status.getStatus().getDescription());
        
        assertNotNull(status.getLanguage());
        assertEquals(71, status.getLanguage().getId());
        assertEquals("Python (3.8.1)", status.getLanguage().getName());
        
        assertTrue(status.isAccepted());
        assertFalse(status.isProcessing());
        assertFalse(status.hasCompilationError());
        assertFalse(status.hasRuntimeError());
    }

    @Test
    void testJudge0StatusResponseDto_ErrorStatuses() {
        // Test compilation error
        Judge0StatusResponseDto compileError = new Judge0StatusResponseDto();
        compileError.setStatus(new Judge0StatusResponseDto.StatusInfo(
                Judge0Constants.Status.COMPILATION_ERROR, "Compilation Error"
        ));
        assertTrue(compileError.hasCompilationError());
        assertFalse(compileError.isAccepted());

        // Test runtime error
        Judge0StatusResponseDto runtimeError = new Judge0StatusResponseDto();
        runtimeError.setStatus(new Judge0StatusResponseDto.StatusInfo(
                Judge0Constants.Status.RUNTIME_ERROR_SIGSEGV, "Runtime Error (SIGSEGV)"
        ));
        assertTrue(runtimeError.hasRuntimeError());
        assertFalse(runtimeError.isAccepted());

        // Test processing status
        Judge0StatusResponseDto processing = new Judge0StatusResponseDto();
        processing.setStatus(new Judge0StatusResponseDto.StatusInfo(
                Judge0Constants.Status.PROCESSING, "Processing"
        ));
        assertTrue(processing.isProcessing());
        assertFalse(processing.isAccepted());
    }

    @Test
    void testJudge0SubmissionRequestDto_MinimalFields() throws Exception {
        // Given - only required fields
        String json = """
                {
                    "source_code": "print(1+1)",
                    "language_id": 71
                }
                """;

        // When
        Judge0SubmissionRequestDto request = objectMapper.readValue(json, Judge0SubmissionRequestDto.class);

        // Then
        assertNotNull(request);
        assertEquals("print(1+1)", request.getSourceCode());
        assertEquals(71, request.getLanguageId());
        assertNull(request.getStdin());
        assertNull(request.getExpectedOutput());
        assertNull(request.getCpuTimeLimit());
        assertNull(request.getMemoryLimit());
        assertFalse(request.getBase64Encoded()); // default value
    }
}