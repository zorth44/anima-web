package com.zorth.anima_web.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class AnimeGenreId implements Serializable {
    private Long animeId;
    private Integer genreId;
} 