package com.foodie.service;

import com.foodie.dto.MenuCategoryDTO;
import com.foodie.request.MenuCategoryRequest;

import java.util.List;

public interface MenuCategoryService {
    MenuCategoryDTO createMenuCategory(String email, MenuCategoryRequest request);
    List<MenuCategoryDTO> getRestaurantMenuCategories(String email);
    List<MenuCategoryDTO> getAllMenuCategories(String email);
    MenuCategoryDTO updateMenuCategory(String email, Long menuCategoryId, MenuCategoryRequest request);
    void deleteMenuCategory(String email, Long menuCategoryId);
}