package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.request.MenuItemRequest;
import com.foodie.service.MenuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // Create a new menu item
    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemDTO>> createMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody MenuItemRequest request) {
        try {
            MenuItemDTO menuItemDTO = menuService.createMenuItem(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(menuItemDTO, "Menu item created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update an existing menu item
    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<MenuItemDTO>> updateMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequest request) {
        try {
            MenuItemDTO menuItemDTO = menuService.updateMenuItem(email, itemId, request);
            return ResponseEntity.ok(new ApiResponse<>(menuItemDTO, "Menu item updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete a menu item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<String>> deleteMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long itemId) {
        try {
            menuService.deleteMenuItem(email, itemId);
            return ResponseEntity.ok(new ApiResponse<>(null, "Menu item deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all menu items for a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenu(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long restaurantId) {
        try {
            List<MenuItemDTO> menuItems = menuService.getMenu(restaurantId, email);
            return ResponseEntity.ok(new ApiResponse<>(menuItems, "Menu items retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all ingredients
    @GetMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<IngredientDTO>>> getIngredients(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<IngredientDTO> ingredients = menuService.getIngredients(email);
            return ResponseEntity.ok(new ApiResponse<>(ingredients, "Ingredients retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update ingredient stock
    @PutMapping("/ingredients/{ingredientId}")
    public ResponseEntity<ApiResponse<IngredientDTO>> updateIngredientStock(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long ingredientId,
            @RequestParam @PositiveOrZero(message = "Quantity must be non-negative") int quantity) {
        try {
            IngredientDTO ingredientDTO = menuService.updateIngredientStock(email, ingredientId, quantity);
            return ResponseEntity.ok(new ApiResponse<>(ingredientDTO, "Ingredient stock updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> searchMenuItems(
//            @CurrentSecurityContext(expression = "authentication?.name") String email,
//            @RequestParam String name) {
//        try {
//            List<MenuItemDTO> menuItems = menuService.searchMenuItems(email, name);
//            return ResponseEntity.ok(new ApiResponse<>(menuItems, "Menu items retrieved successfully", HttpStatus.OK.value()));
//        } catch (ResponseStatusException e) {
//            return ResponseEntity.status(e.getStatusCode())
//                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
//        }
//    }
}