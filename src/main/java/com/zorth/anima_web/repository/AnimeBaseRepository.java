package com.zorth.anima_web.repository;

import org.apache.ibatis.annotations.*;

import com.zorth.anima_web.model.entity.AnimeBase;

@Mapper
public interface AnimeBaseRepository {
    
    @Insert("INSERT INTO anime_base (tmdb_id, title, original_title, overview, release_date, status, " +
            "poster_path, backdrop_path, popularity, vote_average, vote_count, is_deleted, created_at, updated_at) " +
            "VALUES (#{tmdbId}, #{title}, #{originalTitle}, #{overview}, #{releaseDate}, #{status}, " +
            "#{posterPath}, #{backdropPath}, #{popularity}, #{voteAverage}, #{voteCount}, #{isDeleted}, " +
            "#{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AnimeBase anime);
    
    @Update("UPDATE anime_base SET title = #{title}, original_title = #{originalTitle}, " +
            "overview = #{overview}, release_date = #{releaseDate}, status = #{status}, " +
            "poster_path = #{posterPath}, backdrop_path = #{backdropPath}, popularity = #{popularity}, " +
            "vote_average = #{voteAverage}, vote_count = #{voteCount}, is_deleted = #{isDeleted}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(AnimeBase anime);
    
    @Select("SELECT * FROM anime_base WHERE tmdb_id = #{tmdbId}")
    AnimeBase findByTmdbId(Long tmdbId);
    
    default void upsert(AnimeBase anime) {
        AnimeBase existing = findByTmdbId(anime.getTmdbId());
        if (existing != null) {
            anime.setId(existing.getId());
            update(anime);
        } else {
            insert(anime);
        }
    }
} 