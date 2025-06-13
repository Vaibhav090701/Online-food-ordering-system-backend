package com.foodie.service;


import com.foodie.dto.IngredientDTO;
import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.model.Ingredients;
import com.foodie.request.IngredientRequest;

import java.util.List;

public interface IngredientService {
	IngredientDTO createIngredient(String email, IngredientRequest ingredientRequest);
    List<IngredientDTO> getIngredients(String email);
    IngredientDTO getIngredientById(String email, Long ingredientId);
    IngredientDTO updateIngredient(String email, Long ingredientId, IngredientRequest ingredientRequest);
    void deleteIngredient(String email, Long ingredientId) throws Exception;
    IngredientDTO updateStock(String email, Long ingredientId, Integer quantityInStock);   
    
    List<PreDefineIngredientDTO> getAllPreDefineIngredients();
}
