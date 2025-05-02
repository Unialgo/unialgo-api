package com.ua.unialgo.ai.controller;

import com.ua.unialgo.ai.dto.GenerateStatementRequestDto;
import com.ua.unialgo.ai.dto.GenerateStatementResponseDto;
import com.ua.unialgo.ai.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @PostMapping("/gerar-enunciado")
    public ResponseEntity<GenerateStatementResponseDto> generateStatement(@RequestBody GenerateStatementRequestDto request) {
        GenerateStatementResponseDto response = aiService.generateStatement(request);
        return ResponseEntity.ok(response);
    }

}