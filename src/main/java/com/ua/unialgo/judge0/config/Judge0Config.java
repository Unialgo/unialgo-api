package com.ua.unialgo.judge0.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration class for Judge0 integration.
 * Manages API settings and HTTP client configuration.
 */
@Configuration
public class Judge0Config {

    @Value("${judge0.api.url:http://judge0-server:2358}")
    private String judge0ApiUrl;

    @Value("${judge0.api.key:}")
    private String judge0ApiKey;

    @Value("${judge0.default-language-id:62}")
    private Integer defaultLanguageId;

    @Value("${judge0.api.timeout:30}")
    private Integer apiTimeout;

    @Value("${judge0.api.connect-timeout:5}")
    private Integer connectTimeout;

    @Value("${judge0.api.read-timeout:30}")
    private Integer readTimeout;

    /**
     * Creates a RestTemplate bean specifically configured for Judge0 API.
     * This is subtask 1: Set Up HTTP Client
     */
    @Bean(name = "judge0RestTemplate")
    public RestTemplate judge0RestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(judge0ApiUrl)
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    // Getters for configuration values
    public String getJudge0ApiUrl() {
        return judge0ApiUrl;
    }

    public String getJudge0ApiKey() {
        return judge0ApiKey;
    }

    public Integer getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public boolean isAuthenticationEnabled() {
        return judge0ApiKey != null && !judge0ApiKey.trim().isEmpty();
    }

    public Integer getApiTimeout() {
        return apiTimeout;
    }
}