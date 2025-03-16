package com.foodie.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.dto.EventDTO;
import com.foodie.model.Event;
import com.foodie.model.Restaurant;
import com.foodie.repository.EventRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.CreateEventRequest;

@Service
public class EventServiceImp implements EventService {
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private RestaurantRepository restaurantRepository;

	@Override
	public EventDTO createEvent(CreateEventRequest events) throws Exception {
		// TODO Auto-generated method stub
		Event events2=new Event();
		Restaurant restaurent=restaurantRepository.findById(events.getRestaurentId()).orElse(null);
		
		events2.setEndDate(events.getEndDate());
		events2.setEventName(events.getEventName());
		events2.setImageUrl(events.getImageUrl());
		events2.setLocation(events.getLocation());
		events2.setRestaurant(restaurent);
		events2.setStartDate(events.getStartDate());
	
		Event events3= eventRepository.save(events2);
		return convertToEventDTO(events3);
	}

	@Override
	public List<EventDTO> getRestaurentEvents(long restaurentId) throws Exception {
		// TODO Auto-generated method stub
		Restaurant restaurent= restaurantRepository.findById(restaurentId).orElse(null);
		List<Event>events=eventRepository.findByRestaurant(restaurent);
		return events.stream().map(this::convertToEventDTO).collect(Collectors.toList());
	}

	@Override
	public String deleteEvent(long eventId) {
		// TODO Auto-generated method stub
		
		eventRepository.deleteById(eventId);
		return "Event Deleted";
		
		
	}

	public EventDTO updateEvent(long eventId, EventDTO events, long restaurentId) throws Exception {
		// TODO Auto-generated method stub
		Event events2=eventRepository.findById(eventId).orElse(null);
		Restaurant restaurent=restaurantRepository.findById(restaurentId).orElse(null);
		
		events2.setEndDate(events.getEndDate());
		events2.setEventName(events.getEventName());
		events2.setImageUrl(events.getImageUrl());
		events2.setLocation(events.getLocation());
		events2.setRestaurant(restaurent);
		events2.setStartDate(events.getStartDate());
		
		Event events3=eventRepository.save(events2);
		return convertToEventDTO(events3);
	}

	@Override
	public EventDTO findEventById(long eventId) {
		// TODO Auto-generated method stub
		Event events=eventRepository.findById(eventId).orElse(null);
		return convertToEventDTO(events);
	}

	@Override
	public List<EventDTO> getAllEvents() {
		// TODO Auto-generated method stub
		List<Event> events=eventRepository.findAll();
		return events.stream().map(this::convertToEventDTO).collect(Collectors.toList());
	}
	
	public EventDTO convertToEventDTO(Event event) {
		
		EventDTO dto=new EventDTO();
		dto.setEndDate(event.getEndDate());
		dto.setEventName(event.getEventName());
		dto.setImageUrl(event.getImageUrl());
		dto.setLocation(event.getLocation());
		dto.setRestaurentId(event.getRestaurant().getId());
		dto.setStartDate(event.getStartDate());
		 return dto;
		
	}

}
