package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.request.IngredientRequest;
import com.foodie.service.IngredientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    // Create a new ingredient
    @PostMapping
    public ResponseEntity<ApiResponse<IngredientDTO>> createIngredient(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody IngredientRequest ingredientRequest) {
        try {
            IngredientDTO createdIngredient = ingredientService.createIngredient(email, ingredientRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(createdIngredient, "Ingredient created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all ingredients for the restaurant
    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientDTO>>> getIngredients(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<IngredientDTO> ingredients = ingredientService.getIngredients(email);
            return ResponseEntity.ok(new ApiResponse<>(ingredients, "Ingredients retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get an ingredient by ID
    @GetMapping("/{ingredientId}")
    public ResponseEntity<ApiResponse<IngredientDTO>> getIngredientById(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long ingredientId) {
        try {
            IngredientDTO ingredient = ingredientService.getIngredientById(email, ingredientId);
            return ResponseEntity.ok(new ApiResponse<>(ingredient, "Ingredient retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update an existing ingredient
    @PutMapping("/{ingredientId}")
    public ResponseEntity<ApiResponse<IngredientDTO>> updateIngredient(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long ingredientId,
            @Valid @RequestBody IngredientRequest ingredientRequest) {
        try {
            IngredientDTO updatedIngredient = ingredientService.updateIngredient(email, ingredientId, ingredientRequest);
            return ResponseEntity.ok(new ApiResponse<>(updatedIngredient, "Ingredient updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    @PutMapping("/{ingredientId}/stock")
    public ResponseEntity<ApiResponse<IngredientDTO>> updateStock(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long ingredientId,
            @RequestParam @Min(value = 0, message = "Quantity in stock cannot be negative") Integer quantityInStock) {
        try {
            IngredientDTO updatedIngredient = ingredientService.updateStock(email, ingredientId, quantityInStock);
            return ResponseEntity.ok(new ApiResponse<>(updatedIngredient, "Ingredient stock updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    @GetMapping("/preDefineIngredient")
    public ResponseEntity<ApiResponse<List<PreDefineIngredientDTO>>> getAllPreDefineIngredients() {
        try {
            List<PreDefineIngredientDTO> ingredients = ingredientService.getAllPreDefineIngredients();
            return ResponseEntity.ok(new ApiResponse<>(ingredients, "Pre-defined ingredients retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete an ingredient
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<ApiResponse<String>> deleteIngredient(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long ingredientId) {
        try {
            ingredientService.deleteIngredient(email, ingredientId);
            return ResponseEntity.ok(new ApiResponse<>(null, "Ingredient deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}