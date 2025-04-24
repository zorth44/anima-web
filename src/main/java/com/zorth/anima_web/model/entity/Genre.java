package com.zorth.anima_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "genres")
@Data
public class Genre {
    @Id
    private Integer id;
    
    @Column(nullable = false)
    private String name;
} 