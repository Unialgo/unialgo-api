package com.ua.unialgo.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String openaiApiUrl;

    @Value("${openai.api.model:deepseek-chat}")
    private String openaiModel;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public String getOpenaiApiUrl() {
        return openaiApiUrl;
    }

    public String getOpenaiModel() {
        return openaiModel;
    }
}