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

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

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
} 