package com.foodie.service;

import java.util.List;

import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.User;
import com.foodie.request.RestaurentRequest;

public interface RestaurantService {
    // Customer-facing
    List<RestaurantOwnerDTO> searchRestaurants(String query);
    RestaurantOwnerDTO getRestaurantDetails(Long restaurantId);
    
    // Admin-only
    RestaurantOwnerDTO approveRestaurant(String token, Long restaurantId, boolean approve);
    List<RestaurantOwnerDTO> getPendingRestaurants(String token);
    List<RestaurantOwnerDTO>getAllRestaurant(String token);
    RestaurantOwnerDTO getRestaurantById(long id);
	public RestaurantOwnerDTO addFavourites(long restaurentId, User user);
    
    


}
