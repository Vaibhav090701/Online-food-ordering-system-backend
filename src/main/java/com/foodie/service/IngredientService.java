package com.foodie.service;


import com.foodie.dto.IngredientDTO;
import com.foodie.model.Ingredients;
import com.foodie.request.IngredientRequest;

import java.util.List;

public interface IngredientService {
    IngredientDTO createIngredient(String token, IngredientRequest ingredientRequest);

    List<IngredientDTO> getIngredients(String token);

    IngredientDTO getIngredientById(String token, Long ingredientId);

    IngredientDTO updateIngredient(String token, Long ingredientId, IngredientRequest ingredientRequest);

    void deleteIngredient(String token, Long ingredientId);
}
