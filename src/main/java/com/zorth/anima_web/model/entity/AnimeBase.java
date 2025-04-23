package com.zorth.anima_web.model.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnimeBase {
    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private String status;
    private String posterPath;
    private String backdropPath;
    private Double popularity;
    private Double voteAverage;
    private Integer voteCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 