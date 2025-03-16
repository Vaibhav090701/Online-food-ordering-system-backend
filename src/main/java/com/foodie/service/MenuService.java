package com.foodie.service;

import java.util.List;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.request.MenuItemRequest;

public interface MenuService {
    // Restaurant Owner-only
    MenuItemDTO createMenuItem(MenuItemRequest request, String token) throws Exception;
    MenuItemDTO updateMenuItem(String token, Long itemId, MenuItemRequest request)throws Exception;
    void deleteMenuItem(String token, Long itemId) throws Exception;
    List<IngredientDTO> getIngredients(String token)throws Exception;
    IngredientDTO updateIngredientStock(String token, Long ingredientId, int quantity)throws Exception;
    List<MenuItemDTO> getMenu(Long restaurantId, String jwt);


}
