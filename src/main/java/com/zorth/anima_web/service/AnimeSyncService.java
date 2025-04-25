package com.zorth.anima_web.service;

import com.zorth.anima_web.client.TmdbClient;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeSyncService {
    private final TmdbClient tmdbClient;
    private final AnimeRepository animeRepository;
    private final GenreRepository genreRepository;
    private final AnimeGenreRepository animeGenreRepository;
    private final OriginCountryRepository originCountryRepository;
    private final AnimeService animeService;
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @Transactional
    public void syncChanges() {
        log.info("开始同步动漫变更信息");
        try {
            TmdbResponse response = tmdbClient.getAnimeChanges();
            if (response != null && response.getResults() != null) {
                for (TmdbResponse.TmdbAnime anime : response.getResults()) {
                    processChange(anime);
                }
            }
        } catch (Exception e) {
            log.error("同步动漫变更信息时发生错误: {}", e.getMessage(), e);
        }
    }
    
    @Transactional
    protected void processChange(TmdbResponse.TmdbAnime anime) {
        try {
            syncAnime(anime.getId());
            // 同步季节和剧集信息
            animeService.syncAnimeData(anime.getId());
        } catch (Exception e) {
            log.error("处理动漫 {} 的变更时发生错误: {}", anime.getId(), e.getMessage(), e);
        }
    }
    
    @Transactional
    public void syncAnime(Long tmdbId) {
        try {
            TmdbResponse.TmdbAnime animeResponse = tmdbClient.getAnimeDetail(tmdbId);
            if (animeResponse != null) {
                Optional<Anime> existingAnime = animeRepository.findByTmdbIdAndMediaType(tmdbId, MediaType.tv);
                Anime anime = existingAnime.orElseGet(Anime::new);
                
                // 更新动漫基本信息
                updateAnimeFromResponse(anime, animeResponse);
                Anime savedAnime = animeRepository.save(anime);
                
                // 更新原产国信息
                updateOriginCountries(savedAnime, animeResponse.getOriginCountry());
                
                // 更新类型关联
                if (animeResponse.getGenreIds() != null) {
                    updateGenres(savedAnime, animeResponse.getGenreIds());
                }
            }
        } catch (Exception e) {
            log.error("同步动漫 {} 信息时发生错误: {}", tmdbId, e.getMessage(), e);
        }
    }
    
    @Transactional
    protected void updateOriginCountries(Anime anime, List<String> originCountries) {
        try {
            // 删除现有的原产国记录
            originCountryRepository.deleteByAnimeId(anime.getId());
            
            // 添加新的原产国记录
            if (originCountries != null) {
                for (String country : originCountries) {
                    OriginCountry originCountry = new OriginCountry();
                    OriginCountryId id = new OriginCountryId();
                    id.setAnimeId(anime.getId());
                    id.setCountryCode(country);
                    originCountry.setId(id);
                    originCountry.setAnime(anime);
                    originCountryRepository.save(originCountry);
                }
            }
        } catch (Exception e) {
            log.error("更新动漫 {} 的原产国信息时发生错误: {}", anime.getId(), e.getMessage(), e);
        }
    }
    
    private void updateAnimeFromResponse(Anime anime, TmdbResponse.TmdbAnime response) {
        anime.setTmdbId(response.getId());
        anime.setName(response.getName());
        anime.setOriginalName(response.getOriginalName());
        anime.setOriginalLanguage(response.getOriginalLanguage());
        anime.setOverview(response.getOverview());
        if (response.getFirstAirDate() != null) {
            anime.setFirstAirDate(LocalDate.parse(response.getFirstAirDate()));
        }
        anime.setMediaType(MediaType.tv);
        anime.setAdult(response.isAdult());
        anime.setPopularity(BigDecimal.valueOf(response.getPopularity()));
        anime.setVoteAverage(BigDecimal.valueOf(response.getVoteAverage()));
        anime.setVoteCount(response.getVoteCount());
        anime.setPosterPath(response.getPosterPath());
        anime.setBackdropPath(response.getBackdropPath());
        anime.setStatus(response.getStatus());
        anime.setIsActive(true);
        anime.setUpdatedAt(LocalDateTime.now());
    }
    
    @Transactional
    public void syncAllAnime() {
        log.info("开始全量同步动漫信息");
        
        // 首先同步所有类型
        syncGenres();
        
        // 获取第一页数据
        TmdbResponse response = tmdbClient.getAnimeList(1);
        int totalPages = response.getTotalPages();
        
        // 处理第一页
        processPage(response.getResults());
        
        // 处理剩余页面
        for (int page = 2; page <= totalPages; page++) {
            log.info("正在处理第 {} 页，共 {} 页", page, totalPages);
            response = tmdbClient.getAnimeList(page);
            processPage(response.getResults());
        }
        
        log.info("完成全量同步动漫信息");
    }
    
    private void syncGenres() {
        log.info("开始同步类型信息");
        TmdbGenreResponse genreResponse = tmdbClient.getGenres();
        if (genreResponse != null && genreResponse.getGenres() != null) {
            for (TmdbGenreResponse.Genre tmdbGenre : genreResponse.getGenres()) {
                Genre genre = new Genre();
                genre.setId(tmdbGenre.getId());
                genre.setName(tmdbGenre.getName());
                genreRepository.save(genre);
            }
            log.info("成功同步 {} 个类型", genreResponse.getGenres().size());
        }
    }
    
    private void processPage(List<TmdbResponse.TmdbAnime> results) {
        for (TmdbResponse.TmdbAnime show : results) {
            try {
                // 更新或创建动漫记录
                Anime anime = animeRepository.findByTmdbIdAndMediaType(show.getId(), MediaType.tv)
                        .orElse(new Anime());
                
                // 更新动漫信息
                updateAnimeFromResponse(anime, show);
                Anime savedAnime = animeRepository.save(anime);
                
                // 更新类型关联
                if (show.getGenreIds() != null) {
                    updateGenres(savedAnime, show.getGenreIds());
                }
                
                // 更新国家关联
                if (show.getOriginCountry() != null) {
                    updateOriginCountries(savedAnime, show.getOriginCountry());
                }
                
                // 同步季节和剧集信息
                animeService.syncAnimeData(show.getId());
            } catch (Exception e) {
                log.error("处理动漫 ID {} 时发生错误: {}", show.getId(), e.getMessage());
            }
        }
    }
    
    private void updateGenres(Anime anime, List<Integer> genreIds) {
        // 删除旧的类型关联
        animeGenreRepository.deleteByAnimeId(anime.getId());
        
        // 创建新的类型关联
        for (Integer genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("类型未找到: " + genreId));
            
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
} 