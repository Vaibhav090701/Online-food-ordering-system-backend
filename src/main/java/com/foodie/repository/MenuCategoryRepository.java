package com.foodie.repository;

import com.foodie.model.MenuCategory;
import com.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findByRestaurantOrRestaurantIsNullAndDeletedFalse(Restaurant restaurant);
    List<MenuCategory> findByDeletedFalse();
    Optional<MenuCategory> findByIdAndDeletedFalse(Long id);
}