package com.zorth.anima_web.repository;

import org.apache.ibatis.annotations.*;

import com.zorth.anima_web.model.entity.Genre;

@Mapper
public interface GenreRepository {
    
    @Insert("INSERT INTO genre (id, name) VALUES (#{id}, #{name})")
    void insert(Genre genre);
    
    @Update("UPDATE genre SET name = #{name} WHERE id = #{id}")
    void update(Genre genre);
    
    @Select("SELECT * FROM genre WHERE id = #{id}")
    Genre findById(Integer id);
    
    default void upsert(Genre genre) {
        Genre existing = findById(genre.getId());
        if (existing != null) {
            update(genre);
        } else {
            insert(genre);
        }
    }
} 