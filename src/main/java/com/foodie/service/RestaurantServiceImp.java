package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final UserServices userServices;

    @Override
    public List<RestaurantOwnerDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findByDeletedFalse();
        return restaurants.stream()
                .map(this::convertToRestaurantOwnerDTO)
                .toList();
    }

    @Override
    public RestaurantOwnerDTO getRestaurantById(long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        return convertToRestaurantOwnerDTO(restaurant);
    }

    @Override
    @Transactional
    public RestaurantOwnerDTO addFavourites(long restaurantId, String email) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        List<Restaurant> favourites = user.getFavourites();
        boolean isFavourite = favourites.stream().anyMatch(fav -> fav.getId().equals(restaurantId));

        if (isFavourite) {
            favourites.removeIf(fav -> fav.getId().equals(restaurantId));
        } else {
            favourites.add(restaurant);
        }

        userRepository.save(user);
        return convertToRestaurantOwnerDTO(restaurant);
    }

    @Override
    public List<RestaurantOwnerDTO> searchRestaurants(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }
        List<Restaurant> restaurants = restaurantRepository.searchByNameAndDeletedFalse(query.trim());
        return restaurants.stream()
                .map(this::convertToRestaurantOwnerDTO)
                .toList();
    }

    @Override
    @Transactional
    public RestaurantOwnerDTO approveRestaurant(String email, Long restaurantId, boolean approve) {
        User user = validateAdminUser(email);
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        restaurant.setOpen(approve);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return convertToRestaurantOwnerDTO(savedRestaurant);
    }

    @Override
    public List<RestaurantOwnerDTO> getPendingRestaurants(String email) {
        User user = validateAdminUser(email);
        List<Restaurant> pendingRestaurants = restaurantRepository.findByOpenFalseAndDeletedFalse();
        return pendingRestaurants.stream()
                .map(this::convertToRestaurantOwnerDTO)
                .toList();
    }

    private User validateUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private User validateAdminUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: User is not an admin");
        }
        return user;
    }

    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setCity(restaurant.getCity());
        dto.setPhone(restaurant.getPhone());
        dto.setStatus(restaurant.isOpen());
        dto.setDeleted(restaurant.isDeleted());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setRestaurantCategory(restaurant.getRestaurantCategory());
        dto.setImages(restaurant.getImages());
        dto.setIngredients(restaurant.getIngredients().stream()
                .map(this::convertToIngredientDTO)
                .toList());
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .toList());
        return dto;
    }

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setDeleted(menuItem.isDeleted());
        return dto;
    }

    private IngredientDTO convertToIngredientDTO(Ingredients ingredient) {
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