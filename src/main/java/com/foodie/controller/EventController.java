package com.foodie.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodie.dto.EventDTO;
import com.foodie.model.User;
import com.foodie.service.EventService;
import com.foodie.service.RestaurantService;
import com.foodie.service.UserServices;


@RestController
@RequestMapping("/api/events")
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private UserServices userService;
	
	@Autowired
	private RestaurantService restaurantService;

	@GetMapping()
	public ResponseEntity<List<EventDTO>> getAllEvents(@RequestHeader("Authorization") String jwt) throws Exception{
		User user=userService.findUserByJwtToken(jwt);
		
		List<EventDTO>events=eventService.getAllEvents();
		
		return new ResponseEntity<>(events, HttpStatus.OK);
		
	}

}
