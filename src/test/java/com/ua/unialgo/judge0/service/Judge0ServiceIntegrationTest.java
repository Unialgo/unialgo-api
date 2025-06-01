package com.ua.unialgo.judge0.service;

import com.ua.unialgo.judge0.constants.Judge0Constants;
import com.ua.unialgo.judge0.dto.Judge0StatusResponseDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionRequestDto;
import com.ua.unialgo.judge0.dto.Judge0SubmissionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Judge0Service.
 * These tests require Judge0 to be running.
 * 
 * To run these tests, use: mvn test -Dtest=Judge0ServiceIntegrationTest -Djudge0.integration.tests=true
 */
@SpringBootTest
@TestPropertySource(properties = {
    "judge0.api.url=http://localhost:2358",
    "judge0.api.timeout=30",
    "judge0.api.connect-timeout=5",
    "judge0.api.read-timeout=30"
})
@EnabledIfSystemProperty(named = "judge0.integration.tests", matches = "true")
class Judge0ServiceIntegrationTest {

    @Autowired
    private Judge0Service judge0Service;

    @BeforeEach
    void setUp() {
        // Check if Judge0 is available
        if (!judge0Service.isAvailable()) {
            fail("Judge0 is not available. Please ensure Judge0 is running on localhost:2358");
        }
    }

    @Test
    void testSubmitAndGetStatus_PythonHelloWorld() throws InterruptedException {
        // Given
        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('Hello, World!')")
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        assertNotNull(submission);
        assertNotNull(submission.getToken());

        // Wait for execution
        Thread.sleep(2000);

        // Get status
        Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                submission.getToken(), false);

        // Then
        assertNotNull(status);
        assertTrue(status.isAccepted());
        assertEquals("Hello, World!\n", status.getStdout());
        assertNotNull(status.getTime());
        assertNotNull(status.getMemory());
    }

    @Test
    void testSubmitAndGetStatus_JavaWithInput() throws InterruptedException {
        // Given
        String javaCode = """
                import java.util.Scanner;
                public class Main {
                    public static void main(String[] args) {
                        Scanner scanner = new Scanner(System.in);
                        String name = scanner.nextLine();
                        System.out.println("Hello, " + name + "!");
                    }
                }
                """;

        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode(javaCode)
                .languageId(Judge0Constants.Language.JAVA_OPENJDK_13_0_1)
                .stdin("Judge0")
                .expectedOutput("Hello, Judge0!\n")
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        assertNotNull(submission);

        // Wait for execution (Java takes longer)
        Thread.sleep(4000);

        // Get status
        Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                submission.getToken(), false);

        // Then
        assertNotNull(status);
        assertTrue(status.isAccepted());
        assertEquals("Hello, Judge0!\n", status.getStdout());
    }

    @Test
    void testSubmitAndGetStatus_CompilationError() throws InterruptedException {
        // Given - C++ code with syntax error
        String cppCode = """
                #include <iostream>
                int main() {
                    std::cout << "Hello World" // Missing semicolon
                    return 0;
                }
                """;

        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode(cppCode)
                .languageId(Judge0Constants.Language.CPP_GCC_9_2_0)
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        assertNotNull(submission);

        // Wait for compilation
        Thread.sleep(2000);

        // Get status
        Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                submission.getToken(), false);

        // Then
        assertNotNull(status);
        assertTrue(status.hasCompilationError());
        assertNotNull(status.getCompileOutput());
        assertTrue(status.getCompileOutput().contains("error"));
    }

    @Test
    void testSubmitAndGetStatus_RuntimeError() throws InterruptedException {
        // Given - Python code that causes runtime error
        String pythonCode = """
                def divide():
                    return 10 / 0  # Division by zero
                
                print(divide())
                """;

        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode(pythonCode)
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        assertNotNull(submission);

        // Wait for execution
        Thread.sleep(2000);

        // Get status
        Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                submission.getToken(), false);

        // Then
        assertNotNull(status);
        assertTrue(status.hasRuntimeError());
        assertNotNull(status.getStderr());
        assertTrue(status.getStderr().contains("ZeroDivisionError"));
    }

    @Test
    void testSubmitAndGetStatus_TimeLimit() throws InterruptedException {
        // Given - Python code with infinite loop
        String pythonCode = """
                while True:
                    pass
                """;

        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode(pythonCode)
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .cpuTimeLimit(1.0f) // 1 second limit
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        assertNotNull(submission);

        // Wait for timeout
        Thread.sleep(3000);

        // Get status
        Judge0StatusResponseDto status = judge0Service.getSubmissionStatus(
                submission.getToken(), false);

        // Then
        assertNotNull(status);
        assertEquals(Judge0Constants.Status.TIME_LIMIT_EXCEEDED, 
                status.getStatus().getId());
    }

    @Test
    void testDeleteSubmission() throws InterruptedException {
        // Given - Submit a code first
        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode("print('To be deleted')")
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .build();

        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        String token = submission.getToken();

        // Wait for execution
        Thread.sleep(2000);

        // When - Delete submission
        assertDoesNotThrow(() -> judge0Service.deleteSubmission(token));

        // Then - Verify deletion (should handle gracefully if already deleted)
        assertDoesNotThrow(() -> judge0Service.deleteSubmission(token));
    }

    @Test
    void testPollingForStatus() throws InterruptedException {
        // Given
        Judge0SubmissionRequestDto request = Judge0SubmissionRequestDto.builder()
                .sourceCode("import time\ntime.sleep(1)\nprint('Done!')")
                .languageId(Judge0Constants.Language.PYTHON_3_8_1)
                .build();

        // When - Submit code
        Judge0SubmissionResponseDto submission = judge0Service.submitCode(request);
        String token = submission.getToken();

        // Poll for status
        Judge0StatusResponseDto status = null;
        int attempts = 0;
        while (attempts < 10) {
            status = judge0Service.getSubmissionStatus(token, false);
            if (!status.isProcessing()) {
                break;
            }
            Thread.sleep(500);
            attempts++;
        }

        // Then
        assertNotNull(status);
        assertFalse(status.isProcessing());
        assertTrue(status.isAccepted());
        assertEquals("Done!\n", status.getStdout());
    }
}