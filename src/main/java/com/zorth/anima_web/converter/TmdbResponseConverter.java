package com.zorth.anima_web.converter;

import com.zorth.anima_web.model.dto.TmdbAnimeResponse;
import com.zorth.anima_web.model.dto.TmdbResponse;
import com.zorth.anima_web.model.entity.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TmdbResponseConverter {
    
    public TmdbAnimeResponse convertToAnimeResponse(TmdbResponse.TmdbAnime tmdbAnime) {
        TmdbAnimeResponse response = new TmdbAnimeResponse();
        response.setId(tmdbAnime.getId());
        response.setName(tmdbAnime.getName());
        response.setOriginalName(tmdbAnime.getOriginalName());
        response.setOriginalLanguage(tmdbAnime.getOriginalLanguage());
        response.setOverview(tmdbAnime.getOverview());
        
        if (tmdbAnime.getFirstAirDate() != null && !tmdbAnime.getFirstAirDate().isEmpty()) {
            response.setFirstAirDate(LocalDate.parse(tmdbAnime.getFirstAirDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        response.setMediaType(MediaType.tv);
        response.setAdult(tmdbAnime.isAdult());
        response.setPopularity(BigDecimal.valueOf(tmdbAnime.getPopularity()));
        response.setVoteAverage(BigDecimal.valueOf(tmdbAnime.getVoteAverage()));
        response.setVoteCount(tmdbAnime.getVoteCount());
        response.setPosterPath(tmdbAnime.getPosterPath());
        response.setBackdropPath(tmdbAnime.getBackdropPath());
        response.setStatus(tmdbAnime.getStatus());
        return response;
    }
} 