package com.zorth.anima_web.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SyncHistory {
    private Long id;
    private LocalDateTime syncTime;
    private String syncType;
    private Integer affectedRecords;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private String syncStatus;
    private Integer currentPage;
    private Integer totalPages;
    private Integer lastSuccessfulPage;
} 