package com.foodie.service;

import java.util.List;

import com.foodie.dto.EventDTO;
import com.foodie.model.Event;
import com.foodie.request.CreateEventRequest;

public interface EventService {
	
	public EventDTO createEvent(CreateEventRequest eventRequest) throws Exception;
	public List<EventDTO>getRestaurentEvents(long restaurentId) throws Exception;
	public String deleteEvent(long eventId);
	public EventDTO updateEvent(long eventId,EventDTO eventDTO, long restaurentId) throws Exception;
	public EventDTO findEventById(long eventId);
	public List<EventDTO> getAllEvents();

}
