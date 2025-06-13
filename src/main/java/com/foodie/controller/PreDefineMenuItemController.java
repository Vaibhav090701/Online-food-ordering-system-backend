package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.PreDefineMenuItemDTO;
import com.foodie.request.PreDefineMenuItemRequest;
import com.foodie.service.PreDefineMenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/preDefineMenuItem")
@RequiredArgsConstructor
public class PreDefineMenuItemController {

    private final PreDefineMenuItemService preDefineMenuItemService;

    // Create a new predefined menu item
    @PostMapping
    public ResponseEntity<ApiResponse<PreDefineMenuItemDTO>> createPreDefineMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody PreDefineMenuItemRequest request) {
        try {
            PreDefineMenuItemDTO dto = preDefineMenuItemService.createPreDefineMenuItem(request, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(dto, "Predefined menu item created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update a predefined menu item
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PreDefineMenuItemDTO>> updatePreDefineMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id,
            @Valid @RequestBody PreDefineMenuItemRequest request) {
        try {
            PreDefineMenuItemDTO dto = preDefineMenuItemService.updatePreDefineMenuItem(request, email, id);
            return ResponseEntity.ok(new ApiResponse<>(dto, "Predefined menu item updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete a predefined menu item
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePreDefineMenuItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id) {
        try {
            preDefineMenuItemService.deletePreDefineMenuItem(email, id);
            return ResponseEntity.ok(new ApiResponse<>(null, "Predefined menu item deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all predefined menu items
    @GetMapping
    public ResponseEntity<ApiResponse<List<PreDefineMenuItemDTO>>> getAllPreDefineMenuItems(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<PreDefineMenuItemDTO> dtos = preDefineMenuItemService.getAllPreDefineMenuItems(email);
            return ResponseEntity.ok(new ApiResponse<>(dtos, "Predefined menu items retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get predefined menu items by category
    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResponse<List<PreDefineMenuItemDTO>>> getCategoryWiseMenuItems(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id) {
        try {
            List<PreDefineMenuItemDTO> dtos = preDefineMenuItemService.getAllCategoryWisePreDefineMenuItems(id, email);
            return ResponseEntity.ok(new ApiResponse<>(dtos, "Category-wise predefined menu items retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}