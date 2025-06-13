package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.MenuCategoryDTO;
import com.foodie.request.MenuCategoryRequest;
import com.foodie.service.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/restaurant/menu-category")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    // Create a new menu category
    @PostMapping
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> createMenuCategory(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody MenuCategoryRequest request) {
        try {
            MenuCategoryDTO createdCategory = menuCategoryService.createMenuCategory(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(createdCategory, "Menu category created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Create a global menu category
    @PostMapping("/global")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> createGlobalCategory(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody MenuCategoryRequest request) {
        try {
            request.setGlobalCategory(true);
            MenuCategoryDTO createdCategory = menuCategoryService.createMenuCategory(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(createdCategory, "Global menu category created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all menu categories for the restaurant
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getAllMenuCategories(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<MenuCategoryDTO> categories = menuCategoryService.getAllMenuCategories(email);
            return ResponseEntity.ok(new ApiResponse<>(categories, "Menu categories retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant-specific and global menu categories
    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getRestaurantMenuCategories(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<MenuCategoryDTO> categories = menuCategoryService.getRestaurantMenuCategories(email);
            return ResponseEntity.ok(new ApiResponse<>(categories, "Restaurant menu categories retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update a menu category
    @PutMapping("/{menuCategoryId}")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> updateMenuCategory(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long menuCategoryId,
            @Valid @RequestBody MenuCategoryRequest request) {
        try {
            MenuCategoryDTO updatedCategory = menuCategoryService.updateMenuCategory(email, menuCategoryId, request);
            return ResponseEntity.ok(new ApiResponse<>(updatedCategory, "Menu category updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete a menu category
    @DeleteMapping("/{menuCategoryId}")
    public ResponseEntity<ApiResponse<String>> deleteMenuCategory(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long menuCategoryId) {
        try {
            menuCategoryService.deleteMenuCategory(email, menuCategoryId);
            return ResponseEntity.ok(new ApiResponse<>(null, "Menu category deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}