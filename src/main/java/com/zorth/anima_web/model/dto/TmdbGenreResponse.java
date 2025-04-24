package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class TmdbGenreResponse {
    private List<Genre> genres;

    @Data
    public static class Genre {
        private Integer id;
        private String name;
    }
} 