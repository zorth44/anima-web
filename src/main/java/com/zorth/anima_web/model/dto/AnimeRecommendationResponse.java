package com.zorth.anima_web.model.dto;

import com.zorth.anima_web.model.entity.Anime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AnimeRecommendationResponse {
    private Anime anime;
    private String recommendationReason;
} 