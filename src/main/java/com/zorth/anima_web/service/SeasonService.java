package com.zorth.anima_web.service;

import com.zorth.anima_web.client.TmdbClient;
import com.zorth.anima_web.model.dto.SeasonResponse;
import com.zorth.anima_web.model.dto.EpisodeResponse;
import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.model.entity.Season;
import com.zorth.anima_web.model.entity.Episode;
import com.zorth.anima_web.repository.SeasonRepository;
import com.zorth.anima_web.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonService {
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final TmdbClient tmdbClient;

    @Transactional
    public void syncSeasonsAndEpisodes(Anime anime) {
        log.info("开始同步动漫 {} 的季节和剧集信息", anime.getName());
        
        try {
            List<SeasonResponse> seasons = tmdbClient.getSeasons(anime.getTmdbId());
            if (seasons == null || seasons.isEmpty()) {
                log.warn("无法获取动漫 {} 的季节信息", anime.getName());
                return;
            }

            for (SeasonResponse seasonResponse : seasons) {
                try {
                    // 检查季节号是否有效
                    if (seasonResponse.getSeasonNumber() == null || seasonResponse.getSeasonNumber() < 0) {
                        log.warn("跳过季节信息，无效的季节号: {}", seasonResponse.getName());
                        continue;
                    }

                    Season season = seasonRepository.findByAnimeAndSeasonNumber(anime, seasonResponse.getSeasonNumber())
                            .orElse(new Season());

                    season.setAnime(anime);
                    season.setTmdbSeasonId(seasonResponse.getId());
                    season.setSeasonNumber(seasonResponse.getSeasonNumber());
                    season.setName(seasonResponse.getName() != null ? seasonResponse.getName() : "");
                    season.setOverview(seasonResponse.getOverview() != null ? seasonResponse.getOverview() : "");
                    
                    if (seasonResponse.getAirDate() != null) {
                        season.setAirDate(LocalDate.parse(seasonResponse.getAirDate()));
                    } else {
                        season.setAirDate(null);
                    }
                    
                    season.setEpisodeCount(seasonResponse.getEpisodeCount() != null ? 
                            seasonResponse.getEpisodeCount() : 0);
                    season.setPosterPath(seasonResponse.getPosterPath() != null ? 
                            seasonResponse.getPosterPath() : "");

                    Season savedSeason = seasonRepository.save(season);
                    log.info("成功同步季节 {} 的信息", season.getName());
                    
                    // 同步该季节的剧集信息
                    syncEpisodes(savedSeason);
                } catch (Exception e) {
                    log.error("同步季节信息时发生错误: {}", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("同步动漫 {} 的季节和剧集信息时发生错误: {}", anime.getName(), e.getMessage(), e);
        }
    }

    private void syncEpisodes(Season season) {
        log.info("开始同步季节 {} 的剧集信息", season.getName());
        try {
            List<EpisodeResponse> episodeResponses = tmdbClient.getEpisodes(season.getAnime().getTmdbId(), season.getSeasonNumber());
            if (episodeResponses == null || episodeResponses.isEmpty()) {
                log.warn("无法获取季节 {} 的剧集信息", season.getName());
                return;
            }

            log.debug("获取到的剧集信息: {}", episodeResponses);

            for (EpisodeResponse episodeResponse : episodeResponses) {
                try {
                    log.debug("处理剧集: name={}, episodeNumber={}", episodeResponse.getName(), episodeResponse.getEpisodeNumber());
                    
                    // 检查剧集号是否有效
                    if (episodeResponse.getEpisodeNumber() == null || episodeResponse.getEpisodeNumber() < 0) {
                        log.warn("跳过剧集信息，无效的剧集号: {}, episodeNumber={}", episodeResponse.getName(), episodeResponse.getEpisodeNumber());
                        continue;
                    }

                    // 检查是否已存在该剧集
                    Episode episode = episodeRepository.findBySeasonIdAndEpisodeNumber(season.getId(), episodeResponse.getEpisodeNumber())
                            .orElse(new Episode());

                    // 设置必需字段
                    episode.setSeason(season);
                    episode.setTmdbEpisodeId(episodeResponse.getId());
                    episode.setEpisodeNumber(episodeResponse.getEpisodeNumber());
                    episode.setName(episodeResponse.getName() != null ? episodeResponse.getName() : "未命名剧集");
                    
                    // 设置可选字段
                    episode.setOverview(episodeResponse.getOverview() != null ? episodeResponse.getOverview() : "");
                    
                    if (episodeResponse.getAirDate() != null) {
                        if (episodeResponse.getAirDate() instanceof LocalDate) {
                            episode.setAirDate((LocalDate) episodeResponse.getAirDate());
                        } else {
                            episode.setAirDate(LocalDate.parse(episodeResponse.getAirDate().toString()));
                        }
                    } else {
                        episode.setAirDate(null);
                    }
                    
                    episode.setStillPath(episodeResponse.getStillPath() != null ? 
                            episodeResponse.getStillPath() : "");
                    episode.setRuntime(episodeResponse.getRuntime() != null ? 
                            episodeResponse.getRuntime() : 0);

                    episodeRepository.save(episode);
                    log.info("成功同步剧集 {} 的信息", episode.getName());
                } catch (Exception e) {
                    log.error("同步剧集信息时发生错误: {}", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("同步季节 {} 的剧集信息时发生错误: {}", season.getName(), e.getMessage(), e);
        }
    }
} 