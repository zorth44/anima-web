package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChangesResponse {
    private int page;
    private List<Change> results;
    private int totalPages;
    private int totalResults;
    
    @Data
    public static class Change {
        private Long id;
        private boolean adult;
        private String action;
    }
} 