package com.zorth.anima_web.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.zorth.anima_web.config.TmdbConfig;
import com.zorth.anima_web.model.dto.TmdbResponse;
import com.zorth.anima_web.model.dto.TmdbGenreResponse;
import com.zorth.anima_web.model.dto.ChangesResponse;
import com.zorth.anima_web.model.dto.SeasonResponse;
import com.zorth.anima_web.model.dto.TvDetailResponse;
import com.zorth.anima_web.model.dto.SeasonDetailResponse;
import com.zorth.anima_web.model.dto.EpisodeResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbClient {
    
    private final RestTemplate tmdbRestTemplate;
    private final TmdbConfig tmdbConfig;
    
    public TmdbResponse getAnimeList(int page) {
        String url = UriComponentsBuilder.fromPath("/discover/tv")
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("with_keywords", "210024") // Anime keyword
                .queryParam("sort_by", "first_air_date.desc")
                .queryParam("page", page)
                .queryParam("language", "zh-CN")
                .queryParam("include_adult", false)
                .queryParam("include_null_first_air_dates", false)
                .build()
                .toUriString();
        
        log.info("Requesting TMDB API: {}", url);
        ResponseEntity<String> rawResponse = tmdbRestTemplate.getForEntity(url, String.class);
        log.info("Raw TMDB API Response: {}", rawResponse.getBody());
        
        TmdbResponse response = parseResponse(rawResponse.getBody());
        log.info("Parsed TMDB API Response: page={}, totalPages={}, totalResults={}, results.size={}", 
                response.getPage(), response.getTotalPages(), response.getTotalResults(), 
                response.getResults() != null ? response.getResults().size() : 0);
        return response;
    }
    
    private TmdbResponse parseResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, TmdbResponse.class);
        } catch (Exception e) {
            log.error("Error parsing TMDB response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse TMDB response", e);
        }
    }

    public TmdbGenreResponse getGenres() {
        String url = UriComponentsBuilder.fromPath("/genre/tv/list")
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("language", "zh-CN")
                .build()
                .toUriString();
        
        log.info("Requesting TMDB Genres API: {}", url);
        ResponseEntity<TmdbGenreResponse> response = tmdbRestTemplate.getForEntity(url, TmdbGenreResponse.class);
        log.info("Parsed TMDB Genres Response: {}", response.getBody());
        return response.getBody();
    }

    public ChangesResponse getChanges(LocalDateTime startDate, LocalDateTime endDate) {
        String url = UriComponentsBuilder.fromPath("/tv/changes")
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("start_date", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .queryParam("end_date", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build()
                .toUriString();
        
        log.info("Requesting TMDB Changes API: {}", url);
        ResponseEntity<ChangesResponse> response = tmdbRestTemplate.getForEntity(url, ChangesResponse.class);
        log.info("Parsed TMDB Changes Response: {}", response.getBody());
        return response.getBody();
    }

    /**
     * 获取动漫的所有季节信息
     * @param tvId TMDB的TV ID
     * @return 季节信息列表
     */
    public List<SeasonResponse> getSeasons(Long tvId) {
        String url = UriComponentsBuilder.fromPath("/tv/" + tvId)
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("language", "zh-CN")
                .build()
                .toUriString();
        
        TvDetailResponse response = tmdbRestTemplate.getForObject(url, TvDetailResponse.class);
        return response != null ? response.getSeasons() : Collections.emptyList();
    }

    /**
     * 获取特定季节的详细信息
     * @param tvId TMDB的TV ID
     * @param seasonNumber 季节号
     * @return 季节详细信息
     */
    public SeasonDetailResponse getSeasonDetail(Long tvId, Integer seasonNumber) {
        String url = UriComponentsBuilder.fromPath("/tv/" + tvId + "/season/" + seasonNumber)
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("language", "zh-CN")
                .build()
                .toUriString();
        
        return tmdbRestTemplate.getForObject(url, SeasonDetailResponse.class);
    }

    /**
     * 获取特定季节的所有剧集信息
     * @param tvId TMDB的TV ID
     * @param seasonNumber 季节号
     * @return 剧集信息列表
     */
    public List<EpisodeResponse> getEpisodes(Long tvId, Integer seasonNumber) {
        SeasonDetailResponse seasonDetail = getSeasonDetail(tvId, seasonNumber);
        return seasonDetail != null ? seasonDetail.getEpisodes() : Collections.emptyList();
    }

    public TmdbResponse.TmdbAnime getAnimeDetail(Long tmdbId) {
        String url = UriComponentsBuilder.fromPath("/tv/" + tmdbId)
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("language", "zh-CN")
                .build()
                .toUriString();
        
        log.info("Requesting TMDB Anime Detail API: {}", url);
        ResponseEntity<TmdbResponse.TmdbAnime> response = tmdbRestTemplate.getForEntity(url, TmdbResponse.TmdbAnime.class);
        log.info("Parsed TMDB Anime Detail Response: {}", response.getBody());
        return response.getBody();
    }

    public TmdbResponse getAnimeChanges() {
        String url = String.format("%s/tv/changes?api_key=%s", tmdbConfig.getBaseUrl(), tmdbConfig.getApiKey());
        return tmdbRestTemplate.getForObject(url, TmdbResponse.class);
    }
} 