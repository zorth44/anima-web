package com.zorth.anima_web.client;

import com.zorth.anima_web.model.dto.OpenRouterRequest;
import com.zorth.anima_web.model.dto.OpenRouterResponse;
import com.zorth.anima_web.model.dto.AnimeRecommendation;
import com.zorth.anima_web.model.strategy.PromptStrategy;
import com.zorth.anima_web.model.strategy.PromptStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenRouterClient {
    
    private final WebClient openRouterWebClient;
    private final PromptStrategyFactory promptStrategyFactory;
    
    // 通用聊天完成方法
    public Mono<OpenRouterResponse> chatCompletion(String model, List<OpenRouterRequest.Message> messages, 
                                                 Double temperature, Integer maxTokens) {
        OpenRouterRequest request = new OpenRouterRequest();
        request.setModel(model);
        request.setMessages(messages);
        request.setTemperature(temperature);
        request.setMaxTokens(maxTokens);
        
        return openRouterWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    log.error("OpenRouter API error status: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("OpenRouter API error body: {}", body);
                                return Mono.error(new RuntimeException("OpenRouter API error: " + body));
                            });
                })
                .bodyToMono(OpenRouterResponse.class)
                .doOnNext(response -> {
                    if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                        log.error("Invalid response from OpenRouter API: {}", response);
                    }
                })
                .doOnError(error -> log.error("Error calling OpenRouter API: {}", error.getMessage(), error));
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
    
    // 特定策略的动漫推荐方法
    public Mono<List<AnimeRecommendation>> getAnimeRecommendations(String model, List<OpenRouterRequest.Message> messages, 
                                                                 Double temperature, Integer maxTokens) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy("anime-recommendation");
        List<OpenRouterRequest.Message> processedMessages = strategy.processMessages(messages);
        
        return chatCompletion(model, processedMessages, temperature, maxTokens)
                .map(response -> {
                    if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                        log.error("Invalid response from OpenRouter API: {}", response);
                        return new ArrayList<AnimeRecommendation>();
                    }
                    String content = response.getChoices().get(0).getMessage().getContent();
                    log.info("Processing response content: {}", content);
                    return strategy.processResponse(content);
                })
                .doOnError(error -> log.error("Error in getAnimeRecommendations: {}", error.getMessage(), error));
    }
    
    public Mono<List<AnimeRecommendation>> getAnimeRecommendations(String model, List<OpenRouterRequest.Message> messages) {
        return getAnimeRecommendations(model, messages, 0.7, 1000);
    }
    
    public Mono<List<AnimeRecommendation>> getAnimeRecommendations(String model, String text) {
        OpenRouterRequest.Message message = new OpenRouterRequest.Message();
        message.setRole("user");
        
        OpenRouterRequest.Content content = new OpenRouterRequest.Content();
        content.setType("text");
        content.setText(text);
        
        message.setContent(List.of(content));
        
        return getAnimeRecommendations(model, List.of(message));
    }
} 