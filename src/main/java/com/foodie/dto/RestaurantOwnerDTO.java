package com.foodie.dto;

import java.util.List;

import lombok.Data;

@Data
public class RestaurantOwnerDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String twitter;
    private String instagram;
    private boolean status;
    private String description;
    private List<String>images;
    private List<MenuItemDTO> menuItems;
    private List<IngredientDTO> ingredients;


}
