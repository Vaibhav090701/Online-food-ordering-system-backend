package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodie.model.Restaurant;
import com.foodie.model.User;

import jakarta.transaction.Transactional;

@Transactional
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findByOwner(User owner);
    
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> searchByName(@Param("query") String query);

}
