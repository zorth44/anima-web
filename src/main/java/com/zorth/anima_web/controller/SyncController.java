package com.zorth.anima_web.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zorth.anima_web.service.AnimeSyncService;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {
    
    private final AnimeSyncService animeSyncService;
    
    @PostMapping("/start")
    public ResponseEntity<String> startSync() {
        try {
            animeSyncService.syncAnimeData();
            return ResponseEntity.ok("Sync started successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to start sync: " + e.getMessage());
        }
    }
} 