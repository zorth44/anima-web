package com.zorth.anima_web.repository;

import com.zorth.anima_web.model.entity.Anime;
import com.zorth.anima_web.model.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findByAnimeAndSeasonNumber(Anime anime, Integer seasonNumber);
    List<Season> findByAnime(Anime anime);
} 