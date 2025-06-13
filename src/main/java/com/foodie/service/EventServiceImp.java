package com.foodie.service;

import com.foodie.dto.EventDTO;
import com.foodie.model.Event;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.EventRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.CreateEventRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {

    private final EventRepository eventRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserServices userServices;

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventRequest request, String email) {
        User user = validateUser(email);
        Restaurant restaurant = validateRestaurant(request.getRestaurantId(), user);

        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setImageUrl(request.getImageUrl());
        event.setRestaurant(restaurant);
        event.setDeleted(false);

        Event savedEvent = eventRepository.save(event);
        return convertToEventDTO(savedEvent);
    }

    @Override
    public List<EventDTO> getRestaurantEvents(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        List<Event> events = eventRepository.findByRestaurantAndDeletedFalse(restaurant);
        return events.stream()
                .map(this::convertToEventDTO)
                .toList();
    }

    @Override
    @Transactional
    public String deleteEvent(Long eventId, String email) {
        User user = validateUser(email);
        Event event = eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        Restaurant restaurant = event.getRestaurant();
        if (!restaurant.getOwner().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this event");
        }

        event.setDeleted(true);
        eventRepository.save(event);
        return "Event deleted successfully";
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long eventId, EventDTO eventDTO, String email) {
        User user = validateUser(email);
        Event event = eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        Restaurant restaurant = validateRestaurant(eventDTO.getRestaurantId(), user);
        if (!event.getRestaurant().getOwner().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this event");
        }

        event.setEventName(eventDTO.getEventName());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setImageUrl(eventDTO.getImageUrl());
        event.setRestaurant(restaurant);

        Event savedEvent = eventRepository.save(event);
        return convertToEventDTO(savedEvent);
    }

    @Override
    public EventDTO findEventById(Long eventId) {
        Event event = eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return convertToEventDTO(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findByDeletedFalse();
        return events.stream()
                .map(this::convertToEventDTO)
                .toList();
    }

    private User validateUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private Restaurant validateRestaurant(Long restaurantId, User user) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        if (!restaurant.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to manage this restaurant");
        }
        return restaurant;
    }

    private EventDTO convertToEventDTO(Event event) {
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setLocation(event.getLocation());
        dto.setImageUrl(event.getImageUrl());
        dto.setRestaurantId(event.getRestaurant().getId());
        dto.setDeleted(event.isDeleted());
        return dto;
    }
}