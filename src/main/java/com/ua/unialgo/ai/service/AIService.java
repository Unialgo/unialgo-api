package com.ua.unialgo.ai.service;

import com.ua.unialgo.ai.dto.GenerateStatementRequestDto;
import com.ua.unialgo.ai.dto.GenerateStatementResponseDto;
import com.ua.unialgo.ai.model.ChatCompletionRequest;
import com.ua.unialgo.ai.model.ChatCompletionResponse;
import com.ua.unialgo.core.config.AIConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final AIConfig aiConfig;

    public AIService(RestTemplate restTemplate, AIConfig aiConfig) {
        this.restTemplate = restTemplate;
        this.aiConfig = aiConfig;
    }

    public GenerateStatementResponseDto generateStatement(GenerateStatementRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String key = aiConfig.getOpenaiApiKey();
        headers.setBearerAuth(key);

        String promptContent = buildPrompt(request.title(), request.context());
        
        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message("user", promptContent);
        ChatCompletionRequest chatRequest = new ChatCompletionRequest(
                aiConfig.getOpenaiModel(),
                List.of(userMessage)
        );

        HttpEntity<ChatCompletionRequest> requestEntity = new HttpEntity<>(chatRequest, headers);
        
        ChatCompletionResponse response = restTemplate.postForObject(
                aiConfig.getOpenaiApiUrl(),
                requestEntity,
                ChatCompletionResponse.class
        );

        if (response != null && !response.choices().isEmpty()) {
            String statement = response.choices().get(0).message().content();
            return new GenerateStatementResponseDto(statement);
        }

        throw new RuntimeException("Failed to generate statement from OpenAI");
    }

    private String buildPrompt(String title, String context) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a detailed problem statement for a programming exercise with the title '")
                .append(title)
                .append("'.");
        
        if (context != null && !context.isEmpty()) {
            promptBuilder.append(" Additional context: ")
                    .append(context);
        }
        
        promptBuilder.append("\n\nThe statement should include:\n")
                .append("1. A clear problem description\n")
                .append("2. Input and output specifications\n")
                .append("3. Constraints and edge cases\n")
                .append("4. At least one example with input and expected output\n")
                .append("5. A hint about the algorithm or data structure that could be used\n\n")
                .append("Format the response in Portuguese Brazil and as a well-structured problem statement suitable for a programming assessment platform.");
        
        return promptBuilder.toString();
    }
}