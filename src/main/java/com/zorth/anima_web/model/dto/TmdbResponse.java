package com.zorth.anima_web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TmdbResponse {
    private Integer page;
    private List<TmdbTvShow> results;
    
    @JsonProperty("total_pages")
    private Integer totalPages;
    
    @JsonProperty("total_results")
    private Integer totalResults;
    
    @Data
    public static class TmdbTvShow {
        private Long id;
        private String name;
        private String originalName;
        private String overview;
        private String firstAirDate;
        private String status;
        private String posterPath;
        private String backdropPath;
        private Double popularity;
        private Double voteAverage;
        private Integer voteCount;
        private List<Integer> genreIds;
    }
} 