package com.zorth.anima_web.controller;

import com.zorth.anima_web.client.OpenRouterClient;
import com.zorth.anima_web.model.dto.OpenRouterRequest;
import com.zorth.anima_web.model.dto.OpenRouterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/openrouter")
@RequiredArgsConstructor
public class OpenRouterController {

    private final OpenRouterClient openRouterClient;

    @PostMapping("/chat")
    public Mono<ResponseEntity<OpenRouterResponse>> chat(
            @RequestParam(defaultValue = "anthropic/claude-3.7-sonnet") String model,
            @RequestBody List<OpenRouterRequest.Message> messages,
            @RequestParam(required = false) Double temperature,
            @RequestParam(required = false) Integer maxTokens) {
        
        if (temperature != null && maxTokens != null) {
            return openRouterClient.chatCompletion(model, messages, temperature, maxTokens)
                    .map(ResponseEntity::ok);
        }
        
        return openRouterClient.chatCompletion(model, messages)
                .map(ResponseEntity::ok);
    }
    
    @PostMapping("/chat/text")
    public Mono<ResponseEntity<OpenRouterResponse>> chatText(
            @RequestParam(defaultValue = "anthropic/claude-3.7-sonnet") String model,
            @RequestBody String text) {
        return openRouterClient.chatCompletion(model, text)
                .map(ResponseEntity::ok);
    }
} 