package com.zorth.anima_web.controller;

import com.zorth.anima_web.model.dto.AnimeRecommendationResponse;
import com.zorth.anima_web.service.AnimeRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/anime/recommendations")
@RequiredArgsConstructor
public class AnimeRecommendationController {
    
    private final AnimeRecommendationService recommendationService;
    
    @PostMapping
    public Mono<ResponseEntity<List<AnimeRecommendationResponse>>> getRecommendations(
            @RequestBody String userQuery) {
        return recommendationService.getRecommendations(userQuery)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    // 记录错误并返回适当的错误响应
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
} 