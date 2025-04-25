package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class TvDetailResponse {
    private Long id;
    private String name;
    private String overview;
    private List<SeasonResponse> seasons;
    private LocalDate firstAirDate;
    private String posterPath;
    private String backdropPath;
    private Integer numberOfSeasons;
    private Integer numberOfEpisodes;
    private String status;
} 