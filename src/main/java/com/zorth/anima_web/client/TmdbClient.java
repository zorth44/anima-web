package com.zorth.anima_web.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.zorth.anima_web.config.TmdbConfig;
import com.zorth.anima_web.model.dto.TmdbResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbClient {
    
    private final RestTemplate tmdbRestTemplate;
    private final TmdbConfig tmdbConfig;
    
    public TmdbResponse getAnimeList(int page) {
        String url = UriComponentsBuilder.fromPath("/discover/tv")
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("with_genres", 16) // Animation genre
                .queryParam("sort_by", "first_air_date.desc")
                .queryParam("page", page)
                .queryParam("language", "zh-CN")  // 添加语言参数
                .queryParam("include_adult", true)  // 排除成人内容
                .queryParam("include_null_first_air_dates", false)  // 排除没有首播日期的内容
                .build()
                .toUriString();
        
        log.info("Requesting TMDB API: {}", url);
        String rawResponse = tmdbRestTemplate.getForObject(url, String.class);
        log.info("Raw TMDB API Response: {}", rawResponse);
        
        ResponseEntity<TmdbResponse> response = tmdbRestTemplate.getForEntity(url, TmdbResponse.class);
        log.info("Parsed TMDB API Response: {}", response.getBody());
        return response.getBody();
    }
} 