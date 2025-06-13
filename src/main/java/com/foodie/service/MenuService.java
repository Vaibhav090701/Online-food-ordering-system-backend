package com.foodie.service;

import java.util.List;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.request.MenuItemRequest;

public interface MenuService {
    // Restaurant Owner-only
	MenuItemDTO createMenuItem(String email, MenuItemRequest request);
    List<MenuItemDTO> getMenu(Long restaurantId, String email);
    MenuItemDTO updateMenuItem(String email, Long itemId, MenuItemRequest request);
    void deleteMenuItem(String email, Long itemId);
    List<IngredientDTO> getIngredients(String email);
    IngredientDTO updateIngredientStock(String email, Long ingredientId, int quantity);

}
