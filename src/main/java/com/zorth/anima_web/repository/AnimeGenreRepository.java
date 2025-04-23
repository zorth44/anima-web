package com.zorth.anima_web.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.zorth.anima_web.model.entity.AnimeGenre;

@Mapper
public interface AnimeGenreRepository {
    
    @Insert("INSERT INTO anime_genre (anime_id, genre_id) VALUES (#{animeId}, #{genreId})")
    void insert(AnimeGenre animeGenre);
    
    @Select("SELECT * FROM anime_genre WHERE anime_id = #{animeId} AND genre_id = #{genreId}")
    AnimeGenre findByAnimeIdAndGenreId(Long animeId, Integer genreId);
} 