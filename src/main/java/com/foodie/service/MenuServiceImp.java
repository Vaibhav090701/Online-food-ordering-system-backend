package com.foodie.service;


import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.IngredientRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.MenuItemRequest;
import com.foodie.service.MenuService;
import com.foodie.service.UserServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImp implements MenuService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserServices userServices;

    @Override
    public MenuItemDTO createMenuItem(MenuItemRequest request, String token) throws Exception {
        // Fetch the current user (owner) based on the JWT token
        User user = userServices.findUserByJwtToken(token);
        
        // Fetch the restaurant owned by this user
        Restaurant restaurant = restaurantRepository.findByOwner(user);

        // Create a new MenuItem
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setVegetarian(request.getIsVegetarian());
        menuItem.setImages(request.getImages());
        menuItem.setRestaurant(restaurant);

        // Associate ingredients with the MenuItem
        List<Ingredients> ingredients = ingredientRepository.findAllById(request.getIngredientIds());
        menuItem.setIngredients(ingredients);

        // Save the MenuItem to the repository
        menuItemRepository.save(menuItem);

        // Convert MenuItem to DTO and return it
        return convertToMenuItemDTO(menuItem);
    }
    
    @Override
    public List<MenuItemDTO> getMenu(Long restaurantId, String jwt) {
        try {
			User user = userServices.findUserByJwtToken(jwt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Fetch menu items for the given restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList());
    }


    @Override
    public MenuItemDTO updateMenuItem(String token, Long itemId, MenuItemRequest request) throws Exception {
        // Fetch the current user (owner) based on the JWT token
        User user = userServices.findUserByJwtToken(token);
        
        // Fetch the MenuItem by ID
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Update the MenuItem fields
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setImages(request.getImages());

        // Update the ingredients associated with the MenuItem
        List<Ingredients> ingredients = ingredientRepository.findAllById(request.getIngredientIds());
        menuItem.setIngredients(ingredients);

        // Save the updated MenuItem
        menuItemRepository.save(menuItem);

        // Convert updated MenuItem to DTO and return it
        return convertToMenuItemDTO(menuItem);
    }

    @Override
    public void deleteMenuItem(String token, Long itemId) throws Exception {
        // Fetch the current user (owner) based on the JWT token
        User user = userServices.findUserByJwtToken(token);
        
        // Find and delete the MenuItem by ID
        menuItemRepository.deleteById(itemId);
    }

    @Override
    public List<IngredientDTO> getIngredients(String token) throws Exception {
        // Fetch the current user (owner) based on the JWT token
        User user = userServices.findUserByJwtToken(token);
         
        Restaurant restaurant=restaurantRepository.findByOwner(user);

        // Fetch all ingredients available in the restaurant
        List<Ingredients> ingredients = ingredientRepository.findByRestaurant(restaurant);

        // Convert Ingredient entities to DTOs and return them
        return ingredients.stream()
                .map(ingredient -> convertToIngredientDTO(ingredient))
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO updateIngredientStock(String token, Long ingredientId, int quantity) throws Exception {
        // Fetch the current user (owner) based on the JWT token
        User user = userServices.findUserByJwtToken(token);

        // Find the Ingredient by ID
        Ingredients ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        // Update the ingredient stock
        ingredient.setQuantityInStock(quantity);

        // Save the updated Ingredient
        ingredientRepository.save(ingredient);

        // Convert Ingredient entity to DTO and return it
        return convertToIngredientDTO(ingredient);
    }

    // Helper methods to convert entities to DTOs
    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setVegetarian(menuItem.isVegetarian());
        dto.setImages(menuItem.getImages());
        List<IngredientDTO>dtos=new ArrayList<IngredientDTO>();
        for(Ingredients ingredients:menuItem.getIngredients()) {
        	IngredientDTO dto1=new IngredientDTO();
        	dto1.setDescription(ingredients.getDescription());
        	dto1.setName(ingredients.getName());
        	dto1.setId(ingredients.getId());
        	dto1.setQuantityInStock(ingredients.getQuantityInStock());
        	dto1.setUnit(ingredients.getUnit());
        	
        	dtos.add(dto1);
        }
        
        dto.setIngredients(dtos);
        return dto;
    }

    private IngredientDTO convertToIngredientDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }
}
