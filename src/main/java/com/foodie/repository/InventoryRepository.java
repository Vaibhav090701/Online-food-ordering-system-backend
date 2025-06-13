package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodie.model.Inventory;
import com.foodie.model.InventoryItem;
import com.foodie.model.Restaurant;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByRestaurant(Restaurant restaurant); // To fetch all inventory items related to a restaurant

    @Query("SELECT i FROM InventoryItem i WHERE i.restaurant = :restaurant AND i.deleted = false")
    List<InventoryItem> findByRestaurantAndDeletedFalse(Restaurant restaurant);
}
