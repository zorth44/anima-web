package com.zorth.anima_web.model.dto;

import com.zorth.anima_web.model.entity.Anime;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class AnimeQueryResponse {
    private List<Anime> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

    public static AnimeQueryResponse fromPage(Page<Anime> page) {
        AnimeQueryResponse response = new AnimeQueryResponse();
        response.setContent(page.getContent());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setCurrentPage(page.getNumber());
        response.setPageSize(page.getSize());
        return response;
    }
} 