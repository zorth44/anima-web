package com.zorth.anima_web.controller;

import com.zorth.anima_web.service.AnimeService;
import com.zorth.anima_web.service.AnimeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class AnimeSyncController {
    
    private final AnimeService animeService;
    private final AnimeSyncService animeSyncService;
    
    @PostMapping("/all")
    public ResponseEntity<String> syncAll() {
        try {
            animeSyncService.syncAllAnime();
            return ResponseEntity.ok("All anime synced successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to sync all anime: " + e.getMessage());
        }
    }
    
    @PostMapping("/anime/{tmdbId}")
    public ResponseEntity<String> syncAnime(@PathVariable Long tmdbId) {
        try {
            animeSyncService.syncAnime(tmdbId);
            return ResponseEntity.ok("Anime synced successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to sync anime: " + e.getMessage());
        }
    }

    @PostMapping("/anime")
    public ResponseEntity<String> syncAnimeData() {
        animeService.syncAnimeData();
        return ResponseEntity.ok("Anime synchronization started");
    }
} 