package com.foodie.dto;

import java.util.List;

import com.foodie.model.MenuCategory;

import lombok.Data;

@Data
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private boolean available;
    private boolean isVegetarian;
    private int totalOrders;
    private List<String>images;
    private List<IngredientDTO> ingredients;
    private Long restaurantId;
    private String templateType;
    private MenuCategoryDTO category;
    private boolean deleted;


}
