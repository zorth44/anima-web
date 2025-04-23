package com.zorth.anima_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zorth.anima_web.client.TmdbClient;
import com.zorth.anima_web.model.dto.TmdbResponse;
import com.zorth.anima_web.model.entity.AnimeBase;
import com.zorth.anima_web.model.entity.AnimeGenre;
import com.zorth.anima_web.model.entity.Genre;
import com.zorth.anima_web.model.entity.SyncHistory;
import com.zorth.anima_web.repository.AnimeBaseRepository;
import com.zorth.anima_web.repository.AnimeGenreRepository;
import com.zorth.anima_web.repository.GenreRepository;
import com.zorth.anima_web.repository.SyncHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeSyncService {
    
    private final TmdbClient tmdbClient;
    private final AnimeBaseRepository animeBaseRepository;
    private final GenreRepository genreRepository;
    private final AnimeGenreRepository animeGenreRepository;
    private final SyncHistoryRepository syncHistoryRepository;
    
    @Scheduled(cron = "0 0 3 * * *") // Daily at 3 AM
    public void syncAnimeData() {
        log.info("Starting anime data synchronization");
        
        // Check for active sync
        SyncHistory activeSync = syncHistoryRepository.findFirstBySyncStatusOrderByCreatedAtDesc("IN_PROGRESS");
        
        if (activeSync != null) {
            log.info("Found active sync, continuing from page {}", activeSync.getCurrentPage());
            continueSync(activeSync);
        } else {
            log.info("No active sync found, starting new sync");
            startNewSync();
        }
    }
    
    @Transactional
    public void continueSync(SyncHistory activeSync) {
        int currentPage = activeSync.getCurrentPage();
        int totalPages = activeSync.getTotalPages();
        
        for (int page = currentPage; page <= totalPages; page++) {
            try {
                log.info("Processing page {} of {}", page, totalPages);
                processPage(page);
                
                activeSync.setCurrentPage(page);
                activeSync.setLastSuccessfulPage(page);
                syncHistoryRepository.update(activeSync);
                
            } catch (Exception e) {
                log.error("Error processing page {}: {}", page, e.getMessage());
                activeSync.setErrorMessage(e.getMessage());
                syncHistoryRepository.update(activeSync);
                throw e;
            }
        }
        
        activeSync.setSyncStatus("COMPLETED");
        activeSync.setSyncTime(LocalDateTime.now());
        syncHistoryRepository.update(activeSync);
        log.info("Sync completed successfully");
    }
    
    @Transactional
    public void startNewSync() {
        SyncHistory newSync = new SyncHistory();
        newSync.setSyncType("FULL");
        newSync.setSyncStatus("IN_PROGRESS");
        newSync.setStatus("IN_PROGRESS");
        newSync.setCurrentPage(1);
        newSync.setCreatedAt(LocalDateTime.now());
        syncHistoryRepository.insert(newSync);
        
        try {
            TmdbResponse initialResponse = tmdbClient.getAnimeList(1);
            if (initialResponse == null || initialResponse.getTotalPages() == null) {
                throw new RuntimeException("Invalid response from TMDB API: totalPages is null");
            }
            
            int totalPages = initialResponse.getTotalPages();
            log.info("Total pages to sync: {}", totalPages);
            
            newSync.setTotalPages(totalPages);
            syncHistoryRepository.update(newSync);
            
            continueSync(newSync);
            
        } catch (Exception e) {
            log.error("Error starting new sync: {}", e.getMessage());
            newSync.setSyncStatus("FAILED");
            newSync.setErrorMessage(e.getMessage());
            syncHistoryRepository.update(newSync);
            throw e;
        }
    }
    
    private void processPage(int page) {
        TmdbResponse response = tmdbClient.getAnimeList(page);
        List<TmdbResponse.TmdbTvShow> results = response.getResults();
        
        for (TmdbResponse.TmdbTvShow show : results) {
            // Save or update anime base
            AnimeBase anime = convertToAnimeBase(show);
            animeBaseRepository.upsert(anime);
            
            // Save genres if genreIds is not null
            if (show.getGenreIds() != null && !show.getGenreIds().isEmpty()) {
                for (Integer genreId : show.getGenreIds()) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genreRepository.upsert(genre);
                    
                    AnimeGenre animeGenre = new AnimeGenre();
                    animeGenre.setAnimeId(anime.getId());
                    animeGenre.setGenreId(genreId);
                    animeGenreRepository.insert(animeGenre);
                }
            }
        }
    }
    
    private AnimeBase convertToAnimeBase(TmdbResponse.TmdbTvShow show) {
        AnimeBase anime = new AnimeBase();
        anime.setTmdbId(show.getId());
        anime.setTitle(show.getName());
        anime.setOriginalTitle(show.getOriginalName());
        anime.setOverview(show.getOverview());
        anime.setReleaseDate(show.getFirstAirDate() != null ? 
            java.time.LocalDate.parse(show.getFirstAirDate()) : null);
        anime.setStatus(show.getStatus());
        anime.setPosterPath(show.getPosterPath());
        anime.setBackdropPath(show.getBackdropPath());
        anime.setPopularity(show.getPopularity());
        anime.setVoteAverage(show.getVoteAverage());
        anime.setVoteCount(show.getVoteCount());
        anime.setIsDeleted(false);
        anime.setCreatedAt(LocalDateTime.now());
        anime.setUpdatedAt(LocalDateTime.now());
        return anime;
    }
} 