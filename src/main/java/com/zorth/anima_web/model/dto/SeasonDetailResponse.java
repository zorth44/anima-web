package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeasonDetailResponse {
    private Long id;
    private String name;
    private String overview;
    private Integer seasonNumber;
    private List<EpisodeResponse> episodes;
} 