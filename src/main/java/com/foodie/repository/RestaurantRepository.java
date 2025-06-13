package com.foodie.repository;

import com.foodie.model.Restaurant;
import com.foodie.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r WHERE r.deleted = false")
    List<Restaurant> findByDeletedFalse();

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id AND r.deleted = false")
    Optional<Restaurant> findByIdAndDeletedFalse(Long id);

    @Query("SELECT r FROM Restaurant r WHERE r.open = false AND r.deleted = false")
    List<Restaurant> findByOpenFalseAndDeletedFalse();

    @Query("SELECT r FROM Restaurant r WHERE r.name ILIKE %:query% AND r.deleted = false")
    List<Restaurant> searchByNameAndDeletedFalse(String query);
    
    @Query("SELECT r FROM Restaurant r WHERE r.owner = :owner AND r.deleted = false")
    Optional<Restaurant> findByOwnerAndDeletedFalse(User owner);
    

}