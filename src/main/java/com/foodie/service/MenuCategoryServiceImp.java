package com.foodie.service;

import com.foodie.dto.MenuCategoryDTO;
import com.foodie.model.MenuCategory;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.MenuCategoryRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.MenuCategoryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuCategoryServiceImp implements MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserServices userServices;

    @Override
    @Transactional
    public MenuCategoryDTO createMenuCategory(String email, MenuCategoryRequest request) {
        User user = validateAdminUser(email);
        Restaurant restaurant = request.isGlobalCategory() ? null : validateRestaurantOwnership(user);

        MenuCategory menuCategory = new MenuCategory();
        menuCategory.setCategoryName(request.getCategoryName());
        menuCategory.setCategoryDescription(request.getCategoryDescription());
        menuCategory.setCategoryImages(request.getCategoryImages());
        menuCategory.setRestaurant(restaurant);

        MenuCategory savedCategory = menuCategoryRepository.save(menuCategory);
        return convertToMenuCategoryDTO(savedCategory);
    }

    @Override
    public List<MenuCategoryDTO> getRestaurantMenuCategories(String email) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        List<MenuCategory> categories = menuCategoryRepository.findByRestaurantOrRestaurantIsNullAndDeletedFalse(restaurant);
        return categories.stream()
                .map(this::convertToMenuCategoryDTO)
                .toList();
    }

    @Override
    public List<MenuCategoryDTO> getAllMenuCategories(String email) {
        User user = validateAdminUser(email);

        List<MenuCategory> categories = menuCategoryRepository.findByDeletedFalse();
        return categories.stream()
                .map(this::convertToMenuCategoryDTO)
                .toList();
    }

    @Override
    @Transactional
    public MenuCategoryDTO updateMenuCategory(String email, Long menuCategoryId, MenuCategoryRequest request) {
        User user = validateAdminUser(email);
        Restaurant restaurant = request.isGlobalCategory() ? null : validateRestaurantOwnership(user);

        MenuCategory category = menuCategoryRepository.findByIdAndDeletedFalse(menuCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.getRestaurant() != null && !category.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu category does not belong to the user's restaurant");
        }

        category.setCategoryName(request.getCategoryName());
        category.setCategoryDescription(request.getCategoryDescription());
        category.setCategoryImages(request.getCategoryImages());
        category.setRestaurant(restaurant);

        MenuCategory updatedCategory = menuCategoryRepository.save(category);
        return convertToMenuCategoryDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteMenuCategory(String email, Long menuCategoryId) {
        User user = validateAdminUser(email);
        Restaurant restaurant = validateRestaurantOwnership(user);

        MenuCategory category = menuCategoryRepository.findByIdAndDeletedFalse(menuCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.getRestaurant() != null && !category.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu category does not belong to the user's restaurant");
        }

        category.setDeleted(true);
        menuCategoryRepository.save(category);
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

    private MenuCategoryDTO convertToMenuCategoryDTO(MenuCategory menuCategory) {
        MenuCategoryDTO dto = new MenuCategoryDTO();
        dto.setId(menuCategory.getId());
        dto.setCategoryName(menuCategory.getCategoryName());
        dto.setCategoryDescription(menuCategory.getCategoryDescription());
        dto.setCategoryImage(menuCategory.getCategoryImages());
        dto.setDeleted(menuCategory.isDeleted());
        return dto;
    }
}