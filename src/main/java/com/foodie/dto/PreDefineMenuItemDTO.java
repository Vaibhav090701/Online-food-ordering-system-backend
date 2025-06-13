package com.foodie.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreDefineMenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private boolean vegetarian;
    private boolean deleted;
    private List<String> image;
    private String templateType;
    private String category;
    private List<PreDefineIngredientDTO> ingredients;
}