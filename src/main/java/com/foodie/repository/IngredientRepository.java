package com.foodie.repository;

import com.foodie.model.Ingredients;
import com.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredients, Long> {
    List<Ingredients> findByRestaurantAndDeletedFalse(Restaurant restaurant);
    Optional<Ingredients> findByIdAndDeletedFalse(Long id);
    List<Ingredients> findAllByIdAndDeletedFalse(Iterable<Long> ids);
}