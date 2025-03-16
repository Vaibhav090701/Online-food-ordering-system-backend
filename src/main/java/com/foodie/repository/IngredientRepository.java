package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Ingredients;
import com.foodie.model.Restaurant;

public interface IngredientRepository extends JpaRepository<Ingredients, Long> {
	
	List<Ingredients>findByRestaurant(Restaurant restaurant);

}
