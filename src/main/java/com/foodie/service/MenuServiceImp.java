package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuCategoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuCategory;
import com.foodie.model.MenuItem;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.IngredientRepository;
import com.foodie.repository.MenuCategoryRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.MenuItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final UserServices userServices;
    private final MenuCategoryRepository menuCategoryRepository;

    @Override
    @Transactional
    public MenuItemDTO createMenuItem(String email, MenuItemRequest request) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu category is deleted");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setVegetarian(request.isVegetarian());
        menuItem.setImages(request.getImages());
        menuItem.setRestaurant(restaurant);
        menuItem.setMenuCategory(category);
        menuItem.setTemplateType(request.getTemplateType());

        List<Ingredients> ingredients = validateIngredients(request.getIngredientIds(), restaurant.getId());
        menuItem.setIngredients(ingredients);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToMenuItemDTO(savedMenuItem);
    }

    @Override
    public List<MenuItemDTO> getMenu(Long restaurantId, String email) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        List<MenuItem> menuItems = menuItemRepository.findByRestaurantAndDeletedFalse(restaurantId);
        return menuItems.stream()
                .map(this::convertToMenuItemDTO)
                .toList();
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItem(String email, Long itemId, MenuItemRequest request) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        if (!menuItem.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu item does not belong to the user's restaurant");
        }

        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu category is deleted");
        }

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setVegetarian(request.isVegetarian());
        menuItem.setImages(request.getImages());
        menuItem.setMenuCategory(category);
        List<Ingredients> ingredients = validateIngredients(request.getIngredientIds(), restaurant.getId());
        menuItem.setIngredients(ingredients);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToMenuItemDTO(savedMenuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(String email, Long itemId) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        MenuItem item = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        if (!item.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu item does not belong to the user's restaurant");
        }

        item.setDeleted(true);
        menuItemRepository.save(item);
    }

    @Override
    public List<IngredientDTO> getIngredients(String email) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        List<Ingredients> ingredients = ingredientRepository.findByRestaurantAndDeletedFalse(restaurant);
        return ingredients.stream()
                .map(this::convertToIngredientDTO)
                .toList();
    }

    @Override
    @Transactional
    public IngredientDTO updateIngredientStock(String email, Long ingredientId, int quantity) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        Ingredients ingredient = ingredientRepository.findByIdAndDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        if (!ingredient.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to the user's restaurant");
        }

        ingredient.setQuantityInStock(quantity);
        Ingredients savedIngredient = ingredientRepository.save(ingredient);
        return convertToIngredientDTO(savedIngredient);
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

    private Restaurant validateRestaurantOwnership(User user) {
        return restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found for the user"));
    }

    private List<Ingredients> validateIngredients(List<Long> ingredientIds, Long restaurantId) {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Ingredients> ingredients = ingredientRepository.findAllByIdAndDeletedFalse(ingredientIds);
        if (ingredients.size() != ingredientIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more ingredients not found or deleted");
        }
        for (Ingredients ingredient : ingredients) {
            if (!ingredient.getRestaurant().getId().equals(restaurantId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient does not belong to the restaurant");
            }
        }
        return ingredients;
    }

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setDeleted(menuItem.isDeleted());
        dto.setVegetarian(menuItem.isVegetarian());
        dto.setImages(menuItem.getImages());
        dto.setCategory(convertToMenuCategoryDTO(menuItem.getMenuCategory()));
        dto.setTemplateType(menuItem.getTemplateType());
        List<IngredientDTO> ingredientDTOs = menuItem.getIngredients().stream()
                .map(this::convertToIngredientDTO)
                .toList();
        dto.setIngredients(ingredientDTOs);
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

    private MenuCategoryDTO convertToMenuCategoryDTO(MenuCategory menuCategory) {
        if (menuCategory == null) {
            return null;
        }
        MenuCategoryDTO dto = new MenuCategoryDTO();
        dto.setId(menuCategory.getId());
        dto.setCategoryName(menuCategory.getCategoryName());
        dto.setCategoryDescription(menuCategory.getCategoryDescription());
        dto.setCategoryImage(menuCategory.getCategoryImages());
        dto.setDeleted(menuCategory.isDeleted());
        return dto;
    }
}