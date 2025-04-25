package com.zorth.anima_web.controller;

import com.zorth.anima_web.model.dto.AnimeQueryRequest;
import com.zorth.anima_web.model.dto.AnimeQueryResponse;
import com.zorth.anima_web.service.AnimeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
public class AnimeQueryController {
    
    private final AnimeQueryService animeQueryService;
    
    @GetMapping("/search")
    public ResponseEntity<AnimeQueryResponse> searchAnime(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        AnimeQueryRequest request = new AnimeQueryRequest();
        request.setKeyword(keyword);
        if (startDate != null) {
            request.setStartDate(java.time.LocalDate.parse(startDate));
        }
        if (endDate != null) {
            request.setEndDate(java.time.LocalDate.parse(endDate));
        }
        request.setPage(page);
        request.setSize(size);
        
        return ResponseEntity.ok(AnimeQueryResponse.fromPage(animeQueryService.queryAnime(request)));
    }
} 