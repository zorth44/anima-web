package com.zorth.anima_web.repository;

import com.zorth.anima_web.model.entity.OriginCountry;
import com.zorth.anima_web.model.entity.OriginCountryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginCountryRepository extends JpaRepository<OriginCountry, OriginCountryId> {
    void deleteByAnimeId(Long animeId);
} 