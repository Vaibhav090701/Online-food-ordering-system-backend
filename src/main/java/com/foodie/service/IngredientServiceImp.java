package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.IngredientRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.IngredientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImp implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServices userServices;

    @Override
    public IngredientDTO createIngredient(String token, IngredientRequest ingredientRequest) {
        // Validate user by token
        User user = null;
        try {
            user = userServices.findUserByJwtToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalStateException("Unauthorized: User is not an admin.");
        }

        // Find the restaurant owned by the user
        Restaurant restaurant = restaurantRepository.findByOwner(user);
        if (restaurant == null) {
            throw new IllegalStateException("Unauthorized: Restaurant not found for the user.");
        }

        // Create the ingredient
        Ingredients ingredient = new Ingredients();
        ingredient.setName(ingredientRequest.getName());
        ingredient.setDescription(ingredientRequest.getDescription());
        ingredient.setQuantityInStock(ingredientRequest.getQuantityInStock());
        ingredient.setUnit(ingredientRequest.getUnit());
        ingredient.setRestaurant(restaurant);

        // Save and return the converted DTO
        Ingredients savedIngredient = ingredientRepository.save(ingredient);
        return convertToDTO(savedIngredient);
    }

    @Override
    public List<IngredientDTO> getIngredients(String token) {
        // Validate user by token
        User user = null;
        try {
            user = userServices.findUserByJwtToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalStateException("Unauthorized: User is not an admin.");
        }

        // Find the restaurant owned by the user
        Restaurant restaurant = restaurantRepository.findByOwner(user);
        if (restaurant == null) {
            throw new IllegalStateException("Unauthorized: Restaurant not found for the user.");
        }

        List<Ingredients> ingredients = ingredientRepository.findByRestaurant(restaurant);
        return ingredients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO getIngredientById(String token, Long ingredientId) {
        // Validate user by token
        User user = null;
        try {
            user = userServices.findUserByJwtToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalStateException("Unauthorized: User is not an admin.");
        }

        // Find the restaurant owned by the user
        Restaurant restaurant = restaurantRepository.findByOwner(user);
        if (restaurant == null) {
            throw new IllegalStateException("Unauthorized: Restaurant not found for the user.");
        }

        Ingredients ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if (ingredient != null) {
            return convertToDTO(ingredient);
        }
        return null;
    }

    @Override
    public IngredientDTO updateIngredient(String token, Long ingredientId, IngredientRequest ingredientRequest) {
        // Validate user by token
        User user = null;
        try {
            user = userServices.findUserByJwtToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalStateException("Unauthorized: User is not an admin.");
        }

        // Find the restaurant owned by the user
        Restaurant restaurant = restaurantRepository.findByOwner(user);
        if (restaurant == null) {
            throw new IllegalStateException("Unauthorized: Restaurant not found for the user.");
        }

        Ingredients ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if (ingredient != null) {
            ingredient.setName(ingredientRequest.getName());
            ingredient.setDescription(ingredientRequest.getDescription());
            ingredient.setQuantityInStock(ingredientRequest.getQuantityInStock());
            ingredient.setUnit(ingredientRequest.getUnit());
            ingredient.setRestaurant(restaurant);

            Ingredients updatedIngredient = ingredientRepository.save(ingredient);
            return convertToDTO(updatedIngredient);
        }
        return null;
    }

    @Override
    public void deleteIngredient(String token, Long ingredientId) {
        // Validate user by token
        User user = null;
        try {
            user = userServices.findUserByJwtToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalStateException("Unauthorized: User is not an admin.");
        }

        // Find the restaurant owned by the user
        Restaurant restaurant = restaurantRepository.findByOwner(user);
        if (restaurant == null) {
            throw new IllegalStateException("Unauthorized: Restaurant not found for the user.");
        }

        ingredientRepository.deleteById(ingredientId);
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
}
