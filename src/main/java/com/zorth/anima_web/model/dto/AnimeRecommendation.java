package com.zorth.anima_web.model.dto;

import lombok.Data;

@Data
public class AnimeRecommendation {
    private String animeName;
    private String recommendationReason;
    private Long tmdbId;
} 