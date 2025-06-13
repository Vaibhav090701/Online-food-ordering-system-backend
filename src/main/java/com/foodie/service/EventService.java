package com.foodie.service;

import com.foodie.dto.EventDTO;
import com.foodie.request.CreateEventRequest;

import java.util.List;

public interface EventService {
    EventDTO createEvent(CreateEventRequest events, String email);
    List<EventDTO> getRestaurantEvents(Long restaurantId);
    String deleteEvent(Long eventId, String email);
    EventDTO updateEvent(Long eventId, EventDTO eventDTO, String email);
    EventDTO findEventById(Long eventId);
    List<EventDTO> getAllEvents();
}