package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.RestaurentRequest;
import com.foodie.service.RestaurantService;
import com.foodie.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImp implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServices userServices;

    @Override
    public List<RestaurantOwnerDTO> searchRestaurants(String query) {
        // Search restaurants based on name
        List<Restaurant> restaurants = restaurantRepository.searchByName(query);
        return restaurants.stream()
                .map(this::convertToRestaurantOwnerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantOwnerDTO getRestaurantDetails(Long restaurantId) {
        // Fetch restaurant details by ID
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return convertToRestaurantOwnerDTO(restaurant);
    }


    // Admin-only methods
    @Override
    public RestaurantOwnerDTO approveRestaurant(String token, Long restaurantId, boolean approve) {
        // Ensure that the user has admin privileges
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (!user.getRole().equals("ADMIN")) {
            throw new RuntimeException("Only admin can approve restaurants");
        }

        // Fetch the restaurant by ID
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Approve or reject the restaurant based on the 'approve' parameter
        restaurant.setOpen(approve ? true : false);
        restaurantRepository.save(restaurant);

        // Convert to DTO and return
        return convertToRestaurantOwnerDTO(restaurant);
    }

    @Override
    public List<RestaurantOwnerDTO> getPendingRestaurants(String token) {
//        // Ensure that the user has admin privileges
//        User user = null;
//		try {
//			user = userServices.findUserByJwtToken(token);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        if (!user.getRole().equals("ADMIN")) {
//            throw new RuntimeException("Only admin can view pending restaurants");
//        }
//
//        // Fetch restaurants with "PENDING" status
//        List<Restaurant> pendingRestaurants = restaurantRepository.findByStatus("PENDING");
//        return pendingRestaurants.stream()
//                .map(this::convertToRestaurantOwnerDTO)
//                .collect(Collectors.toList());
    	return null;
    }
    
	@Override
	public List<RestaurantOwnerDTO> getAllRestaurant(String token) {
		// TODO Auto-generated method stub
		List<Restaurant>restaurants=restaurantRepository.findAll();
		return restaurants.stream()
				.map(this::convertToRestaurantOwnerDTO)
				.collect(Collectors.toList());
		
	}


    // Helper methods to convert entities to DTOs
    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setStatus(restaurant.isOpen());
        dto.setDescription(restaurant.getDescription());
        dto.setImages(restaurant.getImages());
        
        List<IngredientDTO> dtos=new ArrayList<IngredientDTO>();
        dto.setIngredients(restaurant.getIngredients().stream()
        		.map(this::convertToDTO)
        		.collect(Collectors.toList()));
        
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList()));
        // Add other necessary fields here (like ingredients)
        return dto;
    }

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        // Add ingredients and other details as necessary
        return dto;
    }
    
    // Method to convert Ingredients object to IngredientDTO
    private IngredientDTO convertToDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }

	@Override
	public RestaurantOwnerDTO getRestaurantById(long id) {
		// TODO Auto-generated method stub
		Restaurant restaurant=restaurantRepository.findById(id).orElse(null);
		return convertToRestaurantOwnerDTO(restaurant);
	}

	@Override
	public RestaurantOwnerDTO addFavourites(long restaurentId, User user) {
		// TODO Auto-generated method stub
		Restaurant restaurant=restaurantRepository.findById(restaurentId).orElse(null);
		
		boolean isFavourite=false;
		List<Restaurant>restaurants=user.getFavourites();
		for( Restaurant res: restaurants) {
			if(res.getId().equals(restaurentId)) {
				isFavourite=true;
				break;
			}			
		}
		if(isFavourite) {
			restaurants.removeIf(favourite->favourite.getId().equals(restaurants));
		}
		else {
			restaurants.add(restaurant);
		}
		userRepository.save(user);
		return convertToRestaurantOwnerDTO(restaurant);
	}



	
}
