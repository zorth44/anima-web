package com.zorth.anima_web.config;

import com.zorth.anima_web.service.AnimeService;
import com.zorth.anima_web.service.AnimeSyncService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    
    private final AnimeService animeService;
    private final AnimeSyncService animeSyncService;
    
    // 每天凌晨2点执行完整同步
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleFullSync() {
        animeSyncService.syncAllAnime();
    }
    
    // 每6小时执行一次增量同步
    @Scheduled(cron = "0 0 */6 * * ?")
    public void scheduleChangesSync() {
        animeSyncService.syncChanges();
    }
    
    // 每天凌晨3点执行季节和剧集同步
    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduleSeasonsAndEpisodesSync() {
        animeService.syncAnimeData();
    }
} 