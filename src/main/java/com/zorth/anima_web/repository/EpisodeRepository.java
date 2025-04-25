package com.zorth.anima_web.repository;

import com.zorth.anima_web.model.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    Optional<Episode> findBySeasonIdAndEpisodeNumber(Long seasonId, Integer episodeNumber);
} 