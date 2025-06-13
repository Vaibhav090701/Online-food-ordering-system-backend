package com.foodie.repository;

import com.foodie.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantAndDeletedFalse(Long restaurantId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.id = :id AND m.deleted = false")
    Optional<MenuItem> findByIdAndDeletedFalse(Long id);
}