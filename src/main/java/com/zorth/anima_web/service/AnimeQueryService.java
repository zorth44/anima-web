package com.zorth.anima_web.service;

import com.zorth.anima_web.model.dto.AnimeQueryRequest;
import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
public class AnimeQueryService {
    
    private final AnimeRepository animeRepository;
    
    public Page<Anime> queryAnime(AnimeQueryRequest request) {
        Specification<Anime> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Add keyword search (fuzzy search on name and original name)
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String keywordPattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), keywordPattern),
                    cb.like(cb.lower(root.get("originalName")), keywordPattern)
                ));
            }
            
            // Add date range filter
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("firstAirDate"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("firstAirDate"), request.getEndDate()));
            }
            
            // Only show active anime
            predicates.add(cb.equal(root.get("isActive"), true));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return animeRepository.findAll(
            spec,
            PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "firstAirDate")
            )
        );
    }

    /**
     * 根据动漫名称查询TMDB ID
     * @param animeName 动漫名称
     * @return TMDB ID
     */
    public Mono<Long> getTmdbIdByAnimeName(String animeName) {
        return Mono.fromCallable((Callable<Long>) () -> {
            // 使用模糊查询查找动漫
            Optional<Anime> animeOpt = animeRepository.findFirstByNameContainingIgnoreCase(animeName);
            return animeOpt.map(Anime::getTmdbId).orElse(null);
        });
    }
    
    /**
     * 根据TMDB ID查询动漫信息
     * @param tmdbId TMDB ID
     * @return 动漫信息
     */
    public Mono<Anime> getAnimeByTmdbId(Long tmdbId) {
        return Mono.fromCallable((Callable<Anime>) () -> 
            animeRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new RuntimeException("Anime not found with tmdbId: " + tmdbId))
        );
    }
} 