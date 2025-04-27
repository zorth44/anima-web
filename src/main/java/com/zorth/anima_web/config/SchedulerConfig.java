package com.zorth.anima_web.config;

import com.zorth.anima_web.service.AnimeService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    
    private final AnimeService animeService;
    
    // 每天凌晨3点执行季节和剧集同步
    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduleSeasonsAndEpisodesSync() {
        animeService.syncAnimeData();
    }
} 