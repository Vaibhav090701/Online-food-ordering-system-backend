package com.foodie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PreDefineMenuItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private boolean vegetarian;
    private boolean deleted;

    @ElementCollection
    private List<String> images;

    private String templateType;

    @ManyToOne
    private MenuCategory menuCategory;

    @ManyToMany
    private List<PreDefineIngredients> ingredients;
}