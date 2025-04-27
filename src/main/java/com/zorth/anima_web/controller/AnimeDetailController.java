package com.zorth.anima_web.controller;

import com.zorth.anima_web.model.dto.SeasonDetailResponse;
import com.zorth.anima_web.model.dto.EpisodeResponse;
import com.zorth.anima_web.model.entity.Season;
import com.zorth.anima_web.service.AnimeDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
public class AnimeDetailController {

    private final AnimeDetailService animeDetailService;

    @GetMapping("/{tmdbId}/seasons")
    public ResponseEntity<List<Season>> getSeasons(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(animeDetailService.getSeasonsByTmdbId(tmdbId));
    }

    @GetMapping("/{tmdbId}/seasons/{seasonNumber}")
    public ResponseEntity<SeasonDetailResponse> getSeasonDetail(
            @PathVariable Long tmdbId,
            @PathVariable Integer seasonNumber) {
        return ResponseEntity.ok(animeDetailService.getSeasonDetailByTmdbId(tmdbId, seasonNumber));
    }

    @GetMapping("/{tmdbId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
    public ResponseEntity<EpisodeResponse> getEpisodeDetail(
            @PathVariable Long tmdbId,
            @PathVariable Integer seasonNumber,
            @PathVariable Integer episodeNumber) {
        return ResponseEntity.ok(animeDetailService.getEpisodeDetailByTmdbId(tmdbId, seasonNumber, episodeNumber));
    }
} 