package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "origin_countries")
@Data
public class OriginCountry {
    @EmbeddedId
    private OriginCountryId id;
    
    @ManyToOne
    @MapsId("animeId")
    @JoinColumn(name = "anime_id")
    private Anime anime;
} 