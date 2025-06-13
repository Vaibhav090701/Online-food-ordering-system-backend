package com.foodie.repository;

import com.foodie.model.Event;
import com.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.restaurant = :restaurant AND e.deleted = false")
    List<Event> findByRestaurantAndDeletedFalse(Restaurant restaurant);

    @Query("SELECT e FROM Event e WHERE e.deleted = false")
    List<Event> findByDeletedFalse();

    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.deleted = false")
    Optional<Event> findByIdAndDeletedFalse(Long id);
}