package com.foodie.service;

import java.util.List;

import com.foodie.dto.InventoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.request.RestaurentRequest;

public interface RestaurantOwnerService {
	
    RestaurantOwnerDTO getRestaurantOfUser(String token);
    RestaurantOwnerDTO updateRestaurantStatus(String token, long restaurantId);
    List<OrderDTO> getTodayOrders(String token);
    MenuItemDTO toggleMenuItemAvailability(String token, Long itemId);
    InventoryDTO getInventoryStatus(String token);
    
	public RestaurantOwnerDTO createRestaurent(RestaurentRequest req, String token);
	RestaurantOwnerDTO updateRestaurantDetails(long id,RestaurentRequest req, String jwt);



}
