package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.PreDefineIngredients;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.IngredientRepository;
import com.foodie.repository.PreDefineIngredientRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.IngredientRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientServiceImp implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserServices userServices;
    private final PreDefineIngredientRepository preDefineIngredientRepository;

    @Override
    @Transactional
    public IngredientDTO createIngredient(String email, IngredientRequest ingredientRequest) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = new Ingredients();
        ingredient.setName(ingredientRequest.getName());
        ingredient.setQuantityInStock(ingredientRequest.getQuantityInStock());
        ingredient.setUnit(ingredientRequest.getUnit());
        ingredient.setPrice(ingredientRequest.getPrice());
        ingredient.setRestaurant(restaurant);

        Ingredients savedIngredient = ingredientRepository.save(ingredient);
        return convertToDTO(savedIngredient);
    }

    @Override
    public List<IngredientDTO> getIngredients(String email) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        List<Ingredients> ingredients = ingredientRepository.findByRestaurantAndDeletedFalse(restaurant);
        return ingredients.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public IngredientDTO getIngredientById(String email, Long ingredientId) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = ingredientRepository.findByIdAndDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        if (!restaurant.equals(ingredient.getRestaurant())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to the user's restaurant");
        }
        return convertToDTO(ingredient);
    }

    @Override
    @Transactional
    public IngredientDTO updateIngredient(String email, Long ingredientId, IngredientRequest ingredientRequest) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = ingredientRepository.findByIdAndDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        if (!restaurant.equals(ingredient.getRestaurant())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to the user's restaurant");
        }

        ingredient.setName(ingredientRequest.getName());
        ingredient.setQuantityInStock(ingredientRequest.getQuantityInStock());
        ingredient.setUnit(ingredientRequest.getUnit());
        ingredient.setPrice(ingredientRequest.getPrice());
        ingredient.setRestaurant(restaurant);

        Ingredients updatedIngredient = ingredientRepository.save(ingredient);
        return convertToDTO(updatedIngredient);
    }
    
    @Override
    @Transactional
    public IngredientDTO updateStock(String email, Long ingredientId, Integer quantityInStock) {
        if (quantityInStock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity in stock cannot be negative");
        }

        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = ingredientRepository.findByIdAndDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        if (!restaurant.equals(ingredient.getRestaurant())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to the user's restaurant");
        }

        ingredient.setQuantityInStock(quantityInStock);
        Ingredients updatedIngredient = ingredientRepository.save(ingredient);
        return convertToDTO(updatedIngredient);
    }

    @Override
    @Transactional
    public void deleteIngredient(String email, Long ingredientId) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = ingredientRepository.findByIdAndDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        if (!restaurant.equals(ingredient.getRestaurant())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to the user's restaurant");
        }

        ingredient.setDeleted(true);
        ingredientRepository.save(ingredient);
    }

    private User validateAdminUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: User is not an admin");
        }
        return user;
    }

    private Restaurant validateRestaurantOwnership(User user) {
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found for the user"));
        return restaurant;
    }
    
    @Override
    public List<PreDefineIngredientDTO> getAllPreDefineIngredients() {
        List<PreDefineIngredients> ingredients = preDefineIngredientRepository.findAll();
        return ingredients.stream()
                .map(this::convertToPreDefineDTO)
                .toList();
    }

    private PreDefineIngredientDTO convertToPreDefineDTO(PreDefineIngredients ingredient) {
        PreDefineIngredientDTO dto = new PreDefineIngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }

    private IngredientDTO convertToDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        dto.setPrice(ingredient.getPrice());
        dto.setDeleted(ingredient.isDeleted());
        return dto;
    }
}