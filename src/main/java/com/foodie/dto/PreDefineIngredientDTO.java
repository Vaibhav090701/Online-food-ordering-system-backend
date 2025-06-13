package com.foodie.dto;

import lombok.Data;

@Data
public class PreDefineIngredientDTO {
    private Long id;
    private String name;
    private boolean vegetarian;
    private boolean deleted;
    private String unit;
}