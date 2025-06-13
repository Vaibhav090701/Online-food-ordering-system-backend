package com.foodie.service;

import com.foodie.dto.InventoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.request.RestaurantRequest;

import java.util.List;

public interface RestaurantOwnerService {
    RestaurantOwnerDTO createRestaurant(RestaurantRequest req, String email);
    RestaurantOwnerDTO updateRestaurantDetails(Long id, RestaurantRequest req, String email);
    RestaurantOwnerDTO getRestaurantOfUser(String email);
    RestaurantOwnerDTO updateRestaurantStatus(String email, Long restaurantId);
    List<OrderDTO> getTodayOrders(String email);
    MenuItemDTO toggleMenuItemAvailability(String email, Long itemId);
    InventoryDTO getInventoryStatus(String email);
}