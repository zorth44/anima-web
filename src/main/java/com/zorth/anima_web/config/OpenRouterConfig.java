package com.zorth.anima_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenRouterConfig {
    
    @Value("${openrouter.api.key}")
    private String apiKey;
    
    @Value("${openrouter.api.base-url}")
    private String baseUrl;
    
    @Bean
    public WebClient openRouterWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl.trim())  // 确保移除末尾的空格
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "https://github.com/zorth/anima-web")
                .defaultHeader("X-Title", "Anima Web")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
} 