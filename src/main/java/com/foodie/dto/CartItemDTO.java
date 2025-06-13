package com.foodie.dto;

import java.util.List;

import jakarta.persistence.ElementCollection;
import lombok.Data;

@Data
public class CartItemDTO {
    private MenuItemDTO menuItemDto;
    private String name;
    private int quantity;
    private double price;
	private List<IngredientDTO> ingredients;
	private long id;
	private long restaurantId;

    // Getters & Setters


}
