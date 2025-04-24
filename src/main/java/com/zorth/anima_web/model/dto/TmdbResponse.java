package com.zorth.anima_web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TmdbResponse {
    private int page;
    private List<TmdbAnime> results;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    @JsonProperty("total_results")
    private int totalResults;
    
    @Data
    public static class TmdbAnime {
        private Long id;
        private boolean adult;
        
        @JsonProperty("backdrop_path")
        private String backdropPath;
        
        @JsonProperty("genre_ids")
        private List<Integer> genreIds;
        
        @JsonProperty("origin_country")
        private List<String> originCountry;
        
        @JsonProperty("original_language")
        private String originalLanguage;
        
        @JsonProperty("original_name")
        private String originalName;
        private String overview;
        private double popularity;
        
        @JsonProperty("poster_path")
        private String posterPath;
        
        @JsonProperty("first_air_date")
        private String firstAirDate;
        private String name;
        
        @JsonProperty("vote_average")
        private double voteAverage;
        
        @JsonProperty("vote_count")
        private int voteCount;
        private String status;
    }
} 