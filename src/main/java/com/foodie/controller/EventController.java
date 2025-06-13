package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.EventDTO;
import com.foodie.request.CreateEventRequest;
import com.foodie.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Get all events (public access)
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents() {
        try {
            List<EventDTO> events = eventService.getAllEvents();
            return ResponseEntity.ok(new ApiResponse<>(events, "Events retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get event by ID (public access)
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long eventId) {
        try {
            EventDTO event = eventService.findEventById(eventId);
            return ResponseEntity.ok(new ApiResponse<>(event, "Event retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get events by restaurant ID (public access)
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getRestaurantEvents(@PathVariable Long restaurantId) {
        try {
            List<EventDTO> events = eventService.getRestaurantEvents(restaurantId);
            return ResponseEntity.ok(new ApiResponse<>(events, "Restaurant events retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Create an event (restaurant owner only)
    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody CreateEventRequest eventRequest) {
        try {
            EventDTO event = eventService.createEvent(eventRequest, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(event, "Event created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update an event (restaurant owner only)
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long eventId,
            @Valid @RequestBody EventDTO eventDTO) {
        try {
            EventDTO updatedEvent = eventService.updateEvent(eventId, eventDTO, email);
            return ResponseEntity.ok(new ApiResponse<>(updatedEvent, "Event updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete an event (restaurant owner only)
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<String>> deleteEvent(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long eventId) {
        try {
            String message = eventService.deleteEvent(eventId, email);
            return ResponseEntity.ok(new ApiResponse<>(message, "Event deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}