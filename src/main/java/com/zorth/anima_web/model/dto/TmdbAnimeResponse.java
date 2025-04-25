package com.zorth.anima_web.model.dto;

import com.zorth.anima_web.model.entity.MediaType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TmdbAnimeResponse {
    private Long id;
    private String name;
    private String originalName;
    private String originalLanguage;
    private String overview;
    private LocalDate firstAirDate;
    private LocalDate releaseDate;
    private MediaType mediaType;
    private Boolean adult;
    private BigDecimal popularity;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private String posterPath;
    private String backdropPath;
    private String status;
} 