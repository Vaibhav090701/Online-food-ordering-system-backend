package com.foodie.repository;

import com.foodie.model.PreDefineIngredients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreDefineIngredientRepository extends JpaRepository<PreDefineIngredients, Long> {
    List<PreDefineIngredients> findByDeletedFalse();

	Optional<PreDefineIngredients> findByIdAndDeletedFalse(Long id);
	
    List<PreDefineIngredients> findAllByIdAndDeletedFalse(Iterable<Long> ids);
}