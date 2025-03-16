package com.foodie.controller;

import com.foodie.dto.IngredientDTO;
import com.foodie.request.IngredientRequest;
import com.foodie.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    // Create a new ingredient
    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredient(
    		@RequestBody IngredientRequest ingredientRequest,
            @RequestHeader("Authorization") String token
            ) {
        IngredientDTO createdIngredient = ingredientService.createIngredient(token, ingredientRequest);
        return new ResponseEntity<>(createdIngredient, HttpStatus.CREATED);
    }

    // Get all ingredients for the restaurant
    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getIngredients(@RequestHeader("Authorization") String token) {
        List<IngredientDTO> ingredients = ingredientService.getIngredients(token);
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    // Get an ingredient by ID
    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientDTO> getIngredientById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long ingredientId) {
        IngredientDTO ingredient = ingredientService.getIngredientById(token, ingredientId);
        if (ingredient != null) {
            return new ResponseEntity<>(ingredient, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update an existing ingredient
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientDTO> updateIngredient(
            @RequestHeader("Authorization") String token,
            @PathVariable Long ingredientId,
            @RequestBody IngredientRequest ingredientRequest) {
        IngredientDTO updatedIngredient = ingredientService.updateIngredient(token, ingredientId, ingredientRequest);
        if (updatedIngredient != null) {
            return new ResponseEntity<>(updatedIngredient, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete an ingredient
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @RequestHeader("Authorization") String token,
            @PathVariable Long ingredientId) {
        ingredientService.deleteIngredient(token, ingredientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
