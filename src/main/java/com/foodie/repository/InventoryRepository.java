package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Inventory;
import com.foodie.model.Restaurant;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByRestaurant(Restaurant restaurant); // To fetch all inventory items related to a restaurant

}
