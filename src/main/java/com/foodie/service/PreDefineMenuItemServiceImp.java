package com.foodie.service;

import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.dto.PreDefineMenuItemDTO;
import com.foodie.model.MenuCategory;
import com.foodie.model.PreDefineIngredients;
import com.foodie.model.PreDefineMenuItems;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.MenuCategoryRepository;
import com.foodie.repository.PreDefineIngredientRepository;
import com.foodie.repository.PreDefineMenuItemRepository;
import com.foodie.request.PreDefineMenuItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreDefineMenuItemServiceImp implements PreDefineMenuItemService {

    private final PreDefineMenuItemRepository preDefineMenuItemRepository;
    private final UserServices userServices;
    private final PreDefineIngredientRepository preDefineIngredientRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    @Override
    @Transactional
    public PreDefineMenuItemDTO createPreDefineMenuItem(PreDefineMenuItemRequest req, String email) {
        User user = validateAdminUser(email);

        MenuCategory category = menuCategoryRepository.findById(req.getMenuCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu category is deleted");
        }

        PreDefineMenuItems item = new PreDefineMenuItems();
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setVegetarian(req.isVegetarian());
        item.setImages(req.getImage());
        item.setTemplateType(req.getTemplateType());
        item.setMenuCategory(category);
        item.setDeleted(false);

        List<PreDefineIngredients> ingredients = validateIngredients(req.getIngredientId());
        item.setIngredients(ingredients);

        PreDefineMenuItems savedItem = preDefineMenuItemRepository.save(item);
        return convertPreDefineMenuItemsDTO(savedItem);
    }

    @Override
    @Transactional
    public void deletePreDefineMenuItem(String email, long id) {
        User user = validateAdminUser(email);

        PreDefineMenuItems item = preDefineMenuItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Predefined menu item not found"));
        item.setDeleted(true);
        preDefineMenuItemRepository.save(item);
    }

    @Override
    @Transactional
    public PreDefineMenuItemDTO updatePreDefineMenuItem(PreDefineMenuItemRequest req, String email, long id) {
        User user = validateAdminUser(email);

        PreDefineMenuItems item = preDefineMenuItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Predefined menu item not found"));

        MenuCategory category = menuCategoryRepository.findById(req.getMenuCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu category is deleted");
        }

        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setVegetarian(req.isVegetarian());
        item.setImages(req.getImage());
        item.setTemplateType(req.getTemplateType());
        item.setMenuCategory(category);

        List<PreDefineIngredients> ingredients = validateIngredients(req.getIngredientId());
        item.setIngredients(ingredients);

        PreDefineMenuItems savedItem = preDefineMenuItemRepository.save(item);
        return convertPreDefineMenuItemsDTO(savedItem);
    }

    @Override
    public List<PreDefineMenuItemDTO> getAllPreDefineMenuItems(String email) {
        User user = validateAdminUser(email);

        List<PreDefineMenuItems> items = preDefineMenuItemRepository.findByDeletedFalse();
        return items.stream()
                .map(this::convertPreDefineMenuItemsDTO)
                .toList();
    }

    @Override
    public List<PreDefineMenuItemDTO> getAllCategoryWisePreDefineMenuItems(long categoryId, String email) {
        User user = validateAdminUser(email);

        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu category not found"));
        if (category.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu category is deleted");
        }

        List<PreDefineMenuItems> items = preDefineMenuItemRepository.findByMenuCategoryAndDeletedFalse(category);
        return items.stream()
                .map(this::convertPreDefineMenuItemsDTO)
                .toList();
    }

    private User validateAdminUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: User is not an admin");
        }
        return user;
    }

    private List<PreDefineIngredients> validateIngredients(List<Long> ingredientIds) {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<PreDefineIngredients> ingredients = preDefineIngredientRepository.findAllByIdAndDeletedFalse(ingredientIds);
        if (ingredients.size() != ingredientIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more predefined ingredients not found or deleted");
        }
        return ingredients;
    }

    private PreDefineMenuItemDTO convertPreDefineMenuItemsDTO(PreDefineMenuItems item) {
        PreDefineMenuItemDTO dto = new PreDefineMenuItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setVegetarian(item.isVegetarian());
        dto.setImage(item.getImages());
        dto.setTemplateType(item.getTemplateType());
        dto.setCategory(item.getMenuCategory().getCategoryName());
        dto.setIngredients(item.getIngredients().stream()
                .map(this::convertToDTO)
                .toList());
        dto.setDeleted(item.isDeleted());
        return dto;
    }

    private PreDefineIngredientDTO convertToDTO(PreDefineIngredients ingredient) {
        PreDefineIngredientDTO dto = new PreDefineIngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setVegetarian(ingredient.isVegetarian());
        dto.setDeleted(ingredient.isDeleted());
        return dto;
    }
}