package com.zorth.anima_web.client;

import com.zorth.anima_web.model.dto.OpenRouterRequest;
import com.zorth.anima_web.model.dto.OpenRouterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenRouterClient {
    
    private final WebClient openRouterWebClient;
    
    public Mono<OpenRouterResponse> chatCompletion(String model, List<OpenRouterRequest.Message> messages, 
                                                 Double temperature, Integer maxTokens) {
        OpenRouterRequest request = new OpenRouterRequest();
        request.setModel(model);
        request.setMessages(messages);
        request.setTemperature(temperature);
        request.setMaxTokens(maxTokens);
        
        log.info("Sending request to OpenRouter API: model={}, messages={}", model, messages);
        
        return openRouterWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenRouterResponse.class)
                .doOnSuccess(response -> log.info("Received response from OpenRouter API: {}", response))
                .doOnError(error -> log.error("Error calling OpenRouter API: {}", error.getMessage()));
    }
    
    public Mono<OpenRouterResponse> chatCompletion(String model, List<OpenRouterRequest.Message> messages) {
        return chatCompletion(model, messages, 0.7, 1000);
    }
    
    public Mono<OpenRouterResponse> chatCompletion(String model, String text) {
        OpenRouterRequest.Message message = new OpenRouterRequest.Message();
        message.setRole("user");
        
        OpenRouterRequest.Content content = new OpenRouterRequest.Content();
        content.setType("text");
        content.setText(text);
        
        message.setContent(List.of(content));
        
        return chatCompletion(model, List.of(message));
    }
} 