package com.zorth.anima_web.service;

import com.zorth.anima_web.client.OpenRouterClient;
import com.zorth.anima_web.model.dto.AnimeRecommendationResponse;
import com.zorth.anima_web.config.OpenRouterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
        return openRouterClient.getAnimeRecommendations(openRouterConfig.getDefaultModel(), userQuery)
                .flatMap(recommendations -> {
                    log.info("Received {} recommendations from OpenRouter", recommendations.size());
                    
                    // 为每个推荐查询TMDB ID和动漫信息
                    List<Mono<AnimeRecommendationResponse>> responseMonos = recommendations.stream()
                            .map(recommendation -> {
                                log.info("Querying TMDB ID for anime: {}", recommendation.getAnimeName());
                                // 先查询TMDB ID
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
                                                return Mono.empty(); // 如果找不到TMDB ID，跳过这个推荐
                                            }
                                            // 查询动漫信息
                                            return animeQueryService.getAnimeByTmdbId(tmdbId)
                                                    .doOnNext(anime -> log.info("Found anime details for: {}", anime.getName()))
                                                    .map(anime -> new AnimeRecommendationResponse()
                                                            .setAnime(anime)
                                                            .setRecommendationReason(recommendation.getRecommendationReason()));
                                        });
                            })
                            .collect(Collectors.toList());
                    
                    // 合并所有结果
                    return Mono.zip(responseMonos, objects -> 
                            List.of(objects)
                                    .stream()
                                    .map(obj -> (AnimeRecommendationResponse) obj)
                                    .collect(Collectors.toList())
                    );
                })
                .doOnNext(responses -> log.info("Successfully processed {} recommendations", responses.size()))
                .doOnError(error -> log.error("Error processing recommendations: {}", error.getMessage(), error));
    }
} 