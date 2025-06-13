package com.foodie.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PreDefineIngredients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean vegetarian;
    private boolean deleted;
    private String unit;
}