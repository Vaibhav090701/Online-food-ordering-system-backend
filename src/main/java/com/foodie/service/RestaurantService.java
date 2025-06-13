package com.foodie.service;

import com.foodie.dto.RestaurantOwnerDTO;

import java.util.List;

public interface RestaurantService {
    List<RestaurantOwnerDTO> getAllRestaurants();
    RestaurantOwnerDTO getRestaurantById(long id);
    RestaurantOwnerDTO addFavourites(long restaurantId, String email);
    List<RestaurantOwnerDTO> searchRestaurants(String query);
    RestaurantOwnerDTO approveRestaurant(String email, Long restaurantId, boolean approve);
    List<RestaurantOwnerDTO> getPendingRestaurants(String email);
}