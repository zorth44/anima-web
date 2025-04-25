package com.zorth.anima_web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EpisodeResponse {
    private Long id;
    private String name;
    private String overview;
    
    @JsonProperty("episode_number")
    private Integer episodeNumber;
    
    @JsonProperty("air_date")
    private LocalDate airDate;
    
    private Integer runtime;
    
    @JsonProperty("still_path")
    private String stillPath;
} 