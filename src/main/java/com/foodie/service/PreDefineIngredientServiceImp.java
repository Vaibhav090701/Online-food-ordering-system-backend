package com.foodie.service;

import com.foodie.dto.PreDefineIngredientDTO;
import com.foodie.model.PreDefineIngredients;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.PreDefineIngredientRepository;
import com.foodie.request.PreDefineIngredientRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreDefineIngredientServiceImp implements PreDefineIngredientService {

    private final PreDefineIngredientRepository preDefineIngredientRepository;
    private final UserServices userServices;

    @Override
    @Transactional
    public PreDefineIngredientDTO createPreDefineIngredient(String email, PreDefineIngredientRequest request) {
        User user = validateAdminUser(email);

        PreDefineIngredients ingredient = new PreDefineIngredients();
        ingredient.setName(request.getName());
        ingredient.setVegetarian(request.isVegetarian());
        ingredient.setDeleted(false);

        PreDefineIngredients savedIngredient = preDefineIngredientRepository.save(ingredient);
        return convertToDTO(savedIngredient);
    }

    @Override
    public List<PreDefineIngredientDTO> getPreDefineIngredients(String email) {
        User user = validateAdminUser(email);

        List<PreDefineIngredients> ingredients = preDefineIngredientRepository.findByDeletedFalse();
        return ingredients.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional
    public void deletePreDefineIngredient(String email, Long id) {
        User user = validateAdminUser(email);
        PreDefineIngredients ingredient = preDefineIngredientRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Predefined ingredient not found"));
        ingredient.setDeleted(true);
        preDefineIngredientRepository.save(ingredient);
    }

    private User validateAdminUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: User is not an admin");
        }
        return user;
    }

    private PreDefineIngredientDTO convertToDTO(PreDefineIngredients ingredient) {
        PreDefineIngredientDTO dto = new PreDefineIngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setVegetarian(ingredient.isVegetarian());
        dto.setDeleted(ingredient.isDeleted());
        return dto;
    }
}