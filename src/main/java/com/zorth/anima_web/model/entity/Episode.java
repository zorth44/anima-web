package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "episodes")
@Data
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;
    
    @Column(name = "tmdb_episode_id", nullable = false)
    private Long tmdbEpisodeId;
    
    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;
    
    @Column(nullable = false)
    private String name;
    
    private String overview;
    
    @Column(name = "air_date")
    private LocalDate airDate;
    
    private Integer runtime;
    
    @Column(name = "still_path")
    private String stillPath;
} 