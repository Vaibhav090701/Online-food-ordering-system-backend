package com.foodie.controller;

import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.User;
import com.foodie.service.MenuService;
import com.foodie.service.RestaurantService;
import com.foodie.service.UserServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private UserServices userServices;
        
	@GetMapping()
	public ResponseEntity<List<RestaurantOwnerDTO>>getAllRestaurents(@RequestHeader("Authorization")String jwt) throws Exception
	{
		List<RestaurantOwnerDTO>restaurents= restaurantService.getAllRestaurant(jwt);
		return new ResponseEntity<>(restaurents,HttpStatus.OK);	
	}
	
    @GetMapping("/menu/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getRestaurantMenu(
            @RequestHeader("Authorization") String token,
            @PathVariable Long restaurantId
            ) throws Exception {
        List<MenuItemDTO> menuItemDTO = menuService.getMenu(restaurantId, token);
        return new ResponseEntity<>(menuItemDTO, HttpStatus.OK);
    }

	
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantOwnerDTO> getRestaurantById(@PathVariable Long restaurantId, String jwt) {
        RestaurantOwnerDTO restaurantDTO = restaurantService.getRestaurantById(restaurantId);
        return new ResponseEntity<>(restaurantDTO, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/add-favourites")
	public ResponseEntity<RestaurantOwnerDTO>addToFavourites(@RequestHeader("Authorization") String jwt, @PathVariable long id) throws Exception
	{
		System.out.println("Receiver id "+id);
		User user=userServices.findUserByJwtToken(jwt);
		RestaurantOwnerDTO dto=restaurantService.addFavourites(id, user);
		return new ResponseEntity<>(dto,HttpStatus.OK);	
	}

    // Search restaurants
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantOwnerDTO>> searchRestaurants(@RequestParam String query) {
        List<RestaurantOwnerDTO> restaurants = restaurantService.searchRestaurants(query);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // Admin-only: Approve or reject a restaurant
    @PutMapping("/approve/{restaurantId}")
    public ResponseEntity<RestaurantOwnerDTO> approveRestaurant(
            @RequestHeader("Authorization") String token,
            @PathVariable Long restaurantId,
            @RequestParam boolean approve) {
        RestaurantOwnerDTO restaurantDTO = restaurantService.approveRestaurant(token, restaurantId, approve);
        return new ResponseEntity<>(restaurantDTO, HttpStatus.OK);
    }

    // Admin-only: Get list of pending restaurants
    @GetMapping("/pending")
    public ResponseEntity<List<RestaurantOwnerDTO>> getPendingRestaurants(
            @RequestHeader("Authorization") String token) {
        List<RestaurantOwnerDTO> pendingRestaurants = restaurantService.getPendingRestaurants(token);
        return new ResponseEntity<>(pendingRestaurants, HttpStatus.OK);
    }
}
