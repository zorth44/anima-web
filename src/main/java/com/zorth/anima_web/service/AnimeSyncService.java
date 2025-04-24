package com.zorth.anima_web.service;

import com.zorth.anima_web.client.TmdbClient;
import com.zorth.anima_web.model.dto.ChangesResponse;
import com.zorth.anima_web.model.dto.TmdbResponse;
import com.zorth.anima_web.model.dto.TmdbGenreResponse;
import com.zorth.anima_web.model.entity.*;
import com.zorth.anima_web.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeSyncService {
    private final TmdbClient tmdbClient;
    private final AnimeRepository animeRepository;
    private final GenreRepository genreRepository;
    private final AnimeGenreRepository animeGenreRepository;
    private final OriginCountryRepository originCountryRepository;
    
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void syncChanges() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        ChangesResponse changes = tmdbClient.getChanges(oneHourAgo, now);
        
        for (ChangesResponse.Change change : changes.getResults()) {
            if (change.getAction() != null && change.getAction().equals("updated")) {
                syncAnime(change.getId());
            }
        }
    }
    
    @Transactional
    public void syncAllAnime() {
        log.info("Starting full anime sync");
        
        // 首先同步所有类型
        syncGenres();
        
        // 获取第一页数据
        TmdbResponse response = tmdbClient.getAnimeList(1);
        int totalPages = response.getTotalPages();
        
        // 处理第一页
        processPage(response.getResults());
        
        // 处理剩余页面
        for (int page = 2; page <= totalPages; page++) {
            log.info("Processing page {} of {}", page, totalPages);
            response = tmdbClient.getAnimeList(page);
            processPage(response.getResults());
        }
        
        log.info("Full anime sync completed");
    }
    
    private void syncGenres() {
        log.info("Syncing genres");
        TmdbGenreResponse genreResponse = tmdbClient.getGenres();
        if (genreResponse != null && genreResponse.getGenres() != null) {
            for (TmdbGenreResponse.Genre tmdbGenre : genreResponse.getGenres()) {
                Genre genre = new Genre();
                genre.setId(tmdbGenre.getId());
                genre.setName(tmdbGenre.getName());
                genreRepository.save(genre);
            }
            log.info("Successfully synced {} genres", genreResponse.getGenres().size());
        }
    }
    
    private void processPage(List<TmdbResponse.TmdbAnime> results) {
        for (TmdbResponse.TmdbAnime show : results) {
            try {
                // 更新或创建动漫记录
                Anime anime = animeRepository.findByTmdbIdAndMediaType(show.getId(), MediaType.TV)
                        .orElse(new Anime());
                
                // 更新动漫信息
                updateAnimeFromResponse(anime, show);
                animeRepository.save(anime);
                
                // 更新类型关联
                if (show.getGenreIds() != null) {
                    updateGenres(anime, show.getGenreIds());
                }
                
                // 更新国家关联
                if (show.getOriginCountry() != null) {
                    updateOriginCountries(anime, show.getOriginCountry());
                }
            } catch (Exception e) {
                log.error("Error processing anime with ID {}: {}", show.getId(), e.getMessage());
            }
        }
    }
    
    @Transactional
    public void syncAnime(Long tmdbId) {
        // 获取动漫详情
        TmdbResponse.TmdbAnime detail = tmdbClient.getAnimeList(1).getResults().stream()
                .filter(anime -> anime.getId().equals(tmdbId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Anime not found: " + tmdbId));
        
        // 更新或创建动漫记录
        Anime anime = animeRepository.findByTmdbIdAndMediaType(tmdbId, MediaType.TV)
                .orElse(new Anime());
        
        // 更新动漫信息
        updateAnimeFromResponse(anime, detail);
        animeRepository.save(anime);
        
        // 更新类型关联
        updateGenres(anime, detail.getGenreIds());
        
        // 更新国家关联
        updateOriginCountries(anime, detail.getOriginCountry());
    }
    
    private void updateAnimeFromResponse(Anime anime, TmdbResponse.TmdbAnime response) {
        anime.setTmdbId(response.getId());
        anime.setName(response.getName() != null ? response.getName() : "");
        anime.setOriginalName(response.getOriginalName() != null ? response.getOriginalName() : "");
        anime.setOriginalLanguage(response.getOriginalLanguage() != null ? response.getOriginalLanguage() : "");
        anime.setOverview(response.getOverview() != null ? response.getOverview() : "");
        if (response.getFirstAirDate() != null) {
            try {
                anime.setFirstAirDate(LocalDate.parse(response.getFirstAirDate()));
            } catch (Exception e) {
                log.warn("Invalid first air date for anime {}: {}", response.getId(), response.getFirstAirDate());
            }
        }
        anime.setMediaType(MediaType.TV);
        anime.setAdult(response.isAdult());
        anime.setPopularity(BigDecimal.valueOf(response.getPopularity()));
        anime.setVoteAverage(BigDecimal.valueOf(response.getVoteAverage()));
        anime.setVoteCount(response.getVoteCount());
        anime.setPosterPath(response.getPosterPath() != null ? response.getPosterPath() : "");
        anime.setBackdropPath(response.getBackdropPath() != null ? response.getBackdropPath() : "");
        anime.setStatus(response.getStatus() != null ? response.getStatus() : "");
        anime.setIsActive(true);
    }
    
    private void updateGenres(Anime anime, List<Integer> genreIds) {
        // 删除旧的类型关联
        animeGenreRepository.deleteByAnimeId(anime.getId());
        
        // 创建新的类型关联
        for (Integer genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("Genre not found: " + genreId));
            
            AnimeGenre animeGenre = new AnimeGenre();
            AnimeGenreId id = new AnimeGenreId();
            id.setAnimeId(anime.getId());
            id.setGenreId(genreId);
            animeGenre.setId(id);
            animeGenre.setAnime(anime);
            animeGenre.setGenre(genre);
            
            animeGenreRepository.save(animeGenre);
        }
    }
    
    private void updateOriginCountries(Anime anime, List<String> countries) {
        // 删除旧的国家关联
        originCountryRepository.deleteByAnimeId(anime.getId());
        
        // 创建新的国家关联
        for (String countryCode : countries) {
            OriginCountry originCountry = new OriginCountry();
            OriginCountryId id = new OriginCountryId();
            id.setAnimeId(anime.getId());
            id.setCountryCode(countryCode);
            originCountry.setId(id);
            originCountry.setAnime(anime);
            
            originCountryRepository.save(originCountry);
        }
    }
} 