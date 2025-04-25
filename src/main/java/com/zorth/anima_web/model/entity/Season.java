package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "seasons")
@Data
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;
    
    @Column(name = "tmdb_season_id", nullable = false)
    private Long tmdbSeasonId;
    
    @Column(name = "season_number", nullable = false)
    private Integer seasonNumber;
    
    @Column(nullable = false)
    private String name;
    
    private String overview;
    
    @Column(name = "air_date")
    private LocalDate airDate;
    
    @Column(name = "episode_count")
    private Integer episodeCount;
    
    @Column(name = "poster_path")
    private String posterPath;
} 