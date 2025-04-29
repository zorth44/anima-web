package com.zorth.anima_web.service;

import com.zorth.anima_web.client.OpenRouterClient;
import com.zorth.anima_web.model.dto.AnimeRecommendationResponse;
import com.zorth.anima_web.config.OpenRouterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeRecommendationService {
    
    private final OpenRouterClient openRouterClient;
    private final AnimeQueryService animeQueryService;
    private final OpenRouterConfig openRouterConfig;
    
    public Mono<List<AnimeRecommendationResponse>> getRecommendations(String userQuery) {
        log.info("Getting recommendations for query: {}", userQuery);
        
        return openRouterClient.getAnimeRecommendations(openRouterConfig.getDefaultModel(), userQuery)
                .doOnNext(recommendations -> log.info("Received {} recommendations from OpenRouter", recommendations.size()))
                .flatMapMany(Flux::fromIterable)
                .flatMap(recommendation -> {
                    log.info("Processing recommendation for anime: {}", recommendation.getAnimeName());
                    return animeQueryService.getTmdbIdByAnimeName(recommendation.getAnimeName())
                            .doOnNext(tmdbId -> {
                                if (tmdbId == null) {
                                    log.warn("No TMDB ID found for anime: {}", recommendation.getAnimeName());
                                } else {
                                    log.info("Found TMDB ID {} for anime: {}", tmdbId, recommendation.getAnimeName());
                                }
                            })
                            .flatMap(tmdbId -> {
                                if (tmdbId == null) {
                                    return Mono.empty();
                                }
                                return animeQueryService.getAnimeByTmdbId(tmdbId)
                                        .doOnNext(anime -> log.info("Found anime details for: {}", anime.getName()))
                                        .map(anime -> new AnimeRecommendationResponse()
                                                .setAnime(anime)
                                                .setRecommendationReason(recommendation.getRecommendationReason()))
                                        .onErrorResume(e -> {
                                            log.error("Error getting anime details for TMDB ID {}: {}", tmdbId, e.getMessage());
                                            return Mono.empty();
                                        });
                            });
                })
                .collectList()
                .doOnNext(responses -> {
                    if (responses.isEmpty()) {
                        log.warn("No valid recommendations found after processing");
                    } else {
                        log.info("Successfully processed {} recommendations", responses.size());
                    }
                })
                .doOnError(error -> log.error("Error processing recommendations: {}", error.getMessage(), error));
    }
} 