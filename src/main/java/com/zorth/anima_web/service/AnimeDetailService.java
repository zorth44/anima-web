package com.zorth.anima_web.service;

import com.zorth.anima_web.model.dto.SeasonDetailResponse;
import com.zorth.anima_web.model.dto.EpisodeResponse;
import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.model.entity.Episode;
import com.zorth.anima_web.model.entity.Season;
import com.zorth.anima_web.repository.AnimeRepository;
import com.zorth.anima_web.repository.EpisodeRepository;
import com.zorth.anima_web.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimeDetailService {
    
    private final AnimeRepository animeRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    
    public List<Season> getSeasonsByTmdbId(Long tmdbId) {
        Anime anime = animeRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with tmdbId: " + tmdbId));
        return seasonRepository.findByAnime(anime);
    }
    
    public SeasonDetailResponse getSeasonDetailByTmdbId(Long tmdbId, Integer seasonNumber) {
        Anime anime = animeRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with tmdbId: " + tmdbId));
        
        Season season = seasonRepository.findByAnimeAndSeasonNumber(anime, seasonNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Season not found for anime " + tmdbId + " with number: " + seasonNumber));
        
        List<Episode> episodes = episodeRepository.findBySeason(season);
        
        SeasonDetailResponse response = new SeasonDetailResponse();
        response.setId(season.getId());
        response.setName(season.getName());
        response.setOverview(season.getOverview());
        response.setSeasonNumber(season.getSeasonNumber());
        response.setEpisodes(episodes.stream()
                .map(episode -> {
                    EpisodeResponse episodeResponse = new EpisodeResponse();
                    episodeResponse.setId(episode.getId());
                    episodeResponse.setName(episode.getName());
                    episodeResponse.setOverview(episode.getOverview());
                    episodeResponse.setEpisodeNumber(episode.getEpisodeNumber());
                    episodeResponse.setAirDate(episode.getAirDate());
                    episodeResponse.setRuntime(episode.getRuntime());
                    episodeResponse.setStillPath(episode.getStillPath());
                    return episodeResponse;
                })
                .collect(Collectors.toList()));
        
        return response;
    }
    
    public EpisodeResponse getEpisodeDetailByTmdbId(Long tmdbId, Integer seasonNumber, Integer episodeNumber) {
        Anime anime = animeRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with tmdbId: " + tmdbId));
        
        Season season = seasonRepository.findByAnimeAndSeasonNumber(anime, seasonNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Season not found for anime " + tmdbId + " with number: " + seasonNumber));
        
        Episode episode = episodeRepository.findBySeasonIdAndEpisodeNumber(season.getId(), episodeNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Episode not found for season " + seasonNumber + " with number: " + episodeNumber));
        
        EpisodeResponse response = new EpisodeResponse();
        response.setId(episode.getId());
        response.setName(episode.getName());
        response.setOverview(episode.getOverview());
        response.setEpisodeNumber(episode.getEpisodeNumber());
        response.setAirDate(episode.getAirDate());
        response.setRuntime(episode.getRuntime());
        response.setStillPath(episode.getStillPath());
        
        return response;
    }
} 