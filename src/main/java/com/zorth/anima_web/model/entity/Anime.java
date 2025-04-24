package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "anime")
@Data
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "original_name")
    private String originalName;
    
    @Column(name = "original_language")
    private String originalLanguage;
    
    private String overview;
    
    @Column(name = "first_air_date")
    private LocalDate firstAirDate;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;
    
    private Boolean adult;
    
    private BigDecimal popularity;
    
    @Column(name = "vote_average")
    private BigDecimal voteAverage;
    
    @Column(name = "vote_count")
    private Integer voteCount;
    
    @Column(name = "poster_path")
    private String posterPath;
    
    @Column(name = "backdrop_path")
    private String backdropPath;
    
    private String status;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 