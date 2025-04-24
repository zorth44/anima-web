package com.zorth.anima_web.repository;

import com.zorth.anima_web.model.entity.AnimeGenre;
import com.zorth.anima_web.model.entity.AnimeGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeGenreRepository extends JpaRepository<AnimeGenre, AnimeGenreId> {
    void deleteByAnimeId(Long animeId);
} 