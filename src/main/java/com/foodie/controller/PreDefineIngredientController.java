package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.request.PreDefineIngredientRequest;
import com.foodie.service.PreDefineIngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/preDefineIngredient")
@RequiredArgsConstructor
public class PreDefineIngredientController {

    private final PreDefineIngredientService preDefineIngredientService;

    // Create a new predefined ingredient
    @PostMapping
    public ResponseEntity<ApiResponse<PreDefineIngredientDTO>> createPreDefineIngredient(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody PreDefineIngredientRequest request) {
        try {
            PreDefineIngredientDTO dto = preDefineIngredientService.createPreDefineIngredient(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(dto, "Predefined ingredient created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all predefined ingredients
    @GetMapping
    public ResponseEntity<ApiResponse<List<PreDefineIngredientDTO>>> getAllPreDefineIngredient(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<PreDefineIngredientDTO> dtos = preDefineIngredientService.getPreDefineIngredients(email);
            return ResponseEntity.ok(new ApiResponse<>(dtos, "Predefined ingredients retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}