package com.zorth.anima_web.service;

import com.zorth.anima_web.client.TmdbClient;
import com.zorth.anima_web.converter.TmdbResponseConverter;
import com.zorth.anima_web.model.dto.TmdbAnimeResponse;
import com.zorth.anima_web.model.dto.TmdbResponse;
import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeService {
    
    private final TmdbClient tmdbClient;
    private final AnimeRepository animeRepository;
    private final SeasonService seasonService;
    private final TmdbResponseConverter responseConverter;

    @Transactional
    public void syncAnimeData() {
        syncAnimeData(null);
    }
    
    @Transactional
    public void syncAnimeData(Long specificTmdbId) {
        log.info("开始同步动漫数据");
        
        if (specificTmdbId != null) {
            // 同步单个动漫
            try {
                TmdbResponse.TmdbAnime animeResponse = tmdbClient.getAnimeDetail(specificTmdbId);
                if (animeResponse != null) {
                    TmdbAnimeResponse convertedResponse = responseConverter.convertToAnimeResponse(animeResponse);
                    processAnimeResponse(convertedResponse);
                }
            } catch (Exception e) {
                log.error("同步动漫数据时发生错误: {}", specificTmdbId, e);
            }
        } else {
            // 同步所有动漫
            int page = 1;
            boolean hasMore = true;
            
            while (hasMore) {
                TmdbResponse response = tmdbClient.getAnimeList(page);
                if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                    hasMore = false;
                    continue;
                }
                
                List<TmdbAnimeResponse> animeResponses = response.getResults().stream()
                        .map(responseConverter::convertToAnimeResponse)
                        .collect(Collectors.toList());
                
                for (TmdbAnimeResponse animeResponse : animeResponses) {
                    try {
                        processAnimeResponse(animeResponse);
                    } catch (Exception e) {
                        log.error("同步动漫数据时发生错误: {}", animeResponse.getName(), e);
                    }
                }
                
                page++;
                if (page > response.getTotalPages()) {
                    hasMore = false;
                }
            }
        }
        
        log.info("完成同步动漫数据");
    }
    
    private void processAnimeResponse(TmdbAnimeResponse animeResponse) {
        // 检查动漫是否已存在
        Anime anime = animeRepository.findByTmdbId(animeResponse.getId())
                .orElseGet(() -> {
                    Anime newAnime = new Anime();
                    newAnime.setTmdbId(animeResponse.getId());
                    return newAnime;
                });
        
        // 更新动漫信息
        updateAnimeFromResponse(anime, animeResponse);
        Anime savedAnime = animeRepository.save(anime);
        
        // 同步季节和剧集信息
        seasonService.syncSeasonsAndEpisodes(savedAnime);
    }

    private void updateAnimeFromResponse(Anime anime, TmdbAnimeResponse response) {
        anime.setName(response.getName());
        anime.setOriginalName(response.getOriginalName());
        anime.setOriginalLanguage(response.getOriginalLanguage());
        anime.setOverview(response.getOverview());
        anime.setFirstAirDate(response.getFirstAirDate());
        anime.setReleaseDate(response.getReleaseDate());
        anime.setMediaType(response.getMediaType());
        anime.setAdult(response.getAdult());
        anime.setPopularity(response.getPopularity());
        anime.setVoteAverage(response.getVoteAverage());
        anime.setVoteCount(response.getVoteCount());
        anime.setPosterPath(response.getPosterPath());
        anime.setBackdropPath(response.getBackdropPath());
        anime.setStatus(response.getStatus());
        anime.setIsActive(true);
    }
} 