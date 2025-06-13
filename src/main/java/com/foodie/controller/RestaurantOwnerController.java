package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.EventDTO;
import com.foodie.dto.InventoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.request.CreateEventRequest;
import com.foodie.request.RestaurantRequest;
import com.foodie.service.EventService;
import com.foodie.service.MenuService;
import com.foodie.service.OrderService;
import com.foodie.service.RestaurantOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/restaurant")
@RequiredArgsConstructor
public class RestaurantOwnerController {

    private final RestaurantOwnerService restaurantOwnerService;
    private final OrderService orderService;
    private final MenuService menuService;
    private final EventService eventService;

    // Create a new restaurant
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> createRestaurant(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody RestaurantRequest req) {
        try {
            RestaurantOwnerDTO dto = restaurantOwnerService.createRestaurant(req, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(dto, "Restaurant created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update restaurant details
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> updateRestaurantDetails(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest req) {
        try {
            RestaurantOwnerDTO dto = restaurantOwnerService.updateRestaurantDetails(id, req, email);
            return ResponseEntity.ok(new ApiResponse<>(dto, "Restaurant updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant for the authenticated user
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> getUserRestaurant(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            RestaurantOwnerDTO dto = restaurantOwnerService.getRestaurantOfUser(email);
            return ResponseEntity.ok(new ApiResponse<>(dto, "Restaurant retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update restaurant status (Active/Inactive)
    @PutMapping("/{restaurantId}/status")
    public ResponseEntity<ApiResponse<RestaurantOwnerDTO>> updateRestaurantStatus(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long restaurantId) {
        try {
            RestaurantOwnerDTO updatedRestaurant = restaurantOwnerService.updateRestaurantStatus(email, restaurantId);
            return ResponseEntity.ok(new ApiResponse<>(updatedRestaurant, "Restaurant status updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get orders for today
    @GetMapping("/orders/today")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getTodayOrders(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<OrderDTO> orders = restaurantOwnerService.getTodayOrders(email);
            return ResponseEntity.ok(new ApiResponse<>(orders, "Today's orders retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Toggle menu item availability
    @PutMapping("/menu/{itemId}/availability")
    public ResponseEntity<ApiResponse<MenuItemDTO>> toggleMenuItemAvailability(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long itemId) {
        try {
            MenuItemDTO menuItemDTO = restaurantOwnerService.toggleMenuItemAvailability(email, itemId);
            return ResponseEntity.ok(new ApiResponse<>(menuItemDTO, "Menu item availability updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get inventory status
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryStatus(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            InventoryDTO inventoryDTO = restaurantOwnerService.getInventoryStatus(email);
            return ResponseEntity.ok(new ApiResponse<>(inventoryDTO, "Inventory status retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant orders with pagination and status filter
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getRestaurantOrders(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<OrderDTO> restaurantOrders = orderService.getRestaurantOrders(email, status, page, size);
            return ResponseEntity.ok(new ApiResponse<>(restaurantOrders, "Restaurant orders retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update order status
    @PutMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long orderId,
            @RequestParam String status) {
        try {
            OrderDTO orderDTO = orderService.updateOrderStatus(email, orderId, status);
            return ResponseEntity.ok(new ApiResponse<>(orderDTO, "Order status updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant menu
    @GetMapping("/{id}/menu")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getRestaurantMenu(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long id) {
        try {
            List<MenuItemDTO> menuItems = menuService.getMenu(id, email);
            return ResponseEntity.ok(new ApiResponse<>(menuItems, "Menu items retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all events
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
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

    // Create an event
    @PostMapping("/event")
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

    // Get restaurant events
    @GetMapping("/events/{restaurantId}/restaurant")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getRestaurantEvents(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long restaurantId) {
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

    // Delete an event
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<String>> deleteEventById(
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

    // Update an event
    @PutMapping("/event/{eventId}")
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

    // Get event by ID
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long eventId) {
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
}