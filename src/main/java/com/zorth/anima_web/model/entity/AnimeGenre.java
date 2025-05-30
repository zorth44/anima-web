package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "anime_genres")
@Data
public class AnimeGenre {
    @EmbeddedId
    private AnimeGenreId id;
    
    @ManyToOne
    @MapsId("animeId")
    @JoinColumn(name = "anime_id")
    private Anime anime;
    
    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;
} 