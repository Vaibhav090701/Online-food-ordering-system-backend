package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.service.MenuService;
import com.foodie.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuService menuService;

    // Get all restaurants
    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantOwnerDTO>>> getAllRestaurants() {
        try {
            List<RestaurantOwnerDTO> restaurants = restaurantService.getAllRestaurants();
            return ResponseEntity.ok(new ApiResponse<>(restaurants, "Restaurants retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant menu
    @GetMapping("/menu/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getRestaurantMenu(
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

    // Get restaurant by ID
    @GetMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> getRestaurantById(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long restaurantId) {
        try {
            RestaurantOwnerDTO restaurantDTO = restaurantService.getRestaurantById(restaurantId);
            return ResponseEntity.ok(new ApiResponse<>(restaurantDTO, "Restaurant retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Add or remove restaurant from favourites
    @PutMapping("/{id}/add-favourites")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> addToFavourites(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id) {
        try {
            RestaurantOwnerDTO dto = restaurantService.addFavourites(id, email);
            return ResponseEntity.ok(new ApiResponse<>(dto, "Favourites updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Search restaurants
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RestaurantOwnerDTO>>> searchRestaurants(@RequestParam String query) {
        try {
            List<RestaurantOwnerDTO> restaurants = restaurantService.searchRestaurants(query);
            return ResponseEntity.ok(new ApiResponse<>(restaurants, "Search results retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Admin-only: Approve or reject a restaurant
    @PutMapping("/approve/{restaurantId}")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> approveRestaurant(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long restaurantId,
            @RequestParam boolean approve) {
        try {
            RestaurantOwnerDTO restaurantDTO = restaurantService.approveRestaurant(email, restaurantId, approve);
            return ResponseEntity.ok(new ApiResponse<>(restaurantDTO, "Restaurant approval updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Admin-only: Get list of pending restaurants
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RestaurantOwnerDTO>>> getPendingRestaurants(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<RestaurantOwnerDTO> pendingRestaurants = restaurantService.getPendingRestaurants(email);
            return ResponseEntity.ok(new ApiResponse<>(pendingRestaurants, "Pending restaurants retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}