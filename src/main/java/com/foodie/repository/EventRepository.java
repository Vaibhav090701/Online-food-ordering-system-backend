package com.foodie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Event;
import com.foodie.model.Restaurant;

public interface EventRepository extends JpaRepository<Event, Long> {
	
	public List<Event> findByRestaurant(Restaurant restaurant);

}
