package com.foodie.service;

import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.request.PreDefineIngredientRequest;

import java.util.List;

public interface PreDefineIngredientService {
    PreDefineIngredientDTO createPreDefineIngredient(String email, PreDefineIngredientRequest request);
    List<PreDefineIngredientDTO> getPreDefineIngredients(String email);
}