package com.zorth.anima_web.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AnimeQueryRequest {
    private String keyword;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 0;
    private Integer size = 10;
} 