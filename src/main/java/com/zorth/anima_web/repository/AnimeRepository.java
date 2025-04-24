package com.zorth.anima_web.repository;

import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.model.entity.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    Optional<Anime> findByTmdbIdAndMediaType(Long tmdbId, MediaType mediaType);
    List<Anime> findByIsActiveTrue();
} 