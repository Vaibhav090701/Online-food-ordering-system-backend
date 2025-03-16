package com.foodie.controller;

import com.foodie.dto.EventDTO;
import com.foodie.dto.InventoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.OrderStatus;
import com.foodie.model.User;
import com.foodie.request.CreateEventRequest;
import com.foodie.request.RestaurentRequest;
import com.foodie.service.EventService;
import com.foodie.service.MenuService;
import com.foodie.service.OrderService;
import com.foodie.service.RestaurantOwnerService;
import com.foodie.service.RestaurantService;
import com.foodie.service.UserServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurant")
public class RestaurantOwnerController {

    @Autowired
    private RestaurantOwnerService restaurantOwnerService;
    
    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private EventService eventService;
        
	@PostMapping()
	public ResponseEntity<RestaurantOwnerDTO> createRestaurent(@RequestBody RestaurentRequest req,@RequestHeader("Authorization")String jwt) throws Exception
	{
		RestaurantOwnerDTO dto= restaurantOwnerService.createRestaurent(req, jwt);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);	
	}


    // Get restaurant dashboard
    @GetMapping("/user")
    public ResponseEntity<RestaurantOwnerDTO> getUserRestaurant(@RequestHeader("Authorization") String token) {
        RestaurantOwnerDTO dto = restaurantOwnerService.getRestaurantOfUser(token);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Update restaurant status (Active/Inactive)
    @PutMapping("/{restaurantId}/status")
    public ResponseEntity<RestaurantOwnerDTO> updateRestaurantStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable long restaurantId) {
        RestaurantOwnerDTO updatedRestaurant = restaurantOwnerService.updateRestaurantStatus(token, restaurantId);
        return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
    }

    // Get orders for today
    @GetMapping("/orders/today")
    public ResponseEntity<List<OrderDTO>> getTodayOrders(@RequestHeader("Authorization") String token) {
        List<OrderDTO> orders = restaurantOwnerService.getTodayOrders(token);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Toggle availability of a menu item
    @PutMapping("/menu/{itemId}/availability")
    public ResponseEntity<MenuItemDTO> toggleMenuItemAvailability(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId) {
        MenuItemDTO menuItemDTO = restaurantOwnerService.toggleMenuItemAvailability(token, itemId);
        return new ResponseEntity<>(menuItemDTO, HttpStatus.OK);
    }

    // Get inventory status
    @GetMapping("/inventory")
    public ResponseEntity<InventoryDTO> getInventoryStatus(@RequestHeader("Authorization") String token) {
        InventoryDTO inventoryDTO = restaurantOwnerService.getInventoryStatus(token);
        return new ResponseEntity<>(inventoryDTO, HttpStatus.OK);
    }
    
    // Get restaurant orders (Restaurant owner-only)
	    @GetMapping("/orders")
	    public ResponseEntity<Page<OrderDTO>> getRestaurantOrders(
	            @RequestHeader("Authorization") String token,
	            @RequestParam(value = "status", defaultValue = "ALL") String status,  // Filter by status (default is "ALL")
	            @RequestParam(value = "page", defaultValue = "0") int page,  // Pagination page number (default is page 0)
	            @RequestParam(value = "size", defaultValue = "10") int size  // Pagination size (default is 10)
	            ) {
	    	System.out.println("Status- "+status);
	        Page<OrderDTO> restaurantOrders = orderService.getRestaurantOrders(token, status, page, size);
	        return new ResponseEntity<>(restaurantOrders, HttpStatus.OK);
	    }

    // Update order status (Restaurant owner-only)
    @PutMapping("/status/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO orderDTO = orderService.updateOrderStatus(token, orderId, status);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
    
    // Get restaurant details by ID
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantOwnerDTO> getRestaurantDetails(@PathVariable Long restaurantId) {
        RestaurantOwnerDTO restaurantDTO = restaurantService.getRestaurantDetails(restaurantId);
        return new ResponseEntity<>(restaurantDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemDTO>> getRestaurantMenu(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
            ) throws Exception {
        List<MenuItemDTO> menuItemDTO = menuService.getMenu(id, token);
        return new ResponseEntity<>(menuItemDTO, HttpStatus.OK);
    }
    
	@GetMapping("/event")
	public ResponseEntity<List<EventDTO>> getAllEvents(@RequestHeader("Authorization") String jwt) throws Exception{
		
		List<EventDTO>events=eventService.getAllEvents();
		
		return new ResponseEntity<>(events, HttpStatus.OK);
		
	}
	
	@PostMapping("/event")
	public ResponseEntity<EventDTO> createEvent(@RequestBody CreateEventRequest eventRequest,  @RequestHeader("Authorization")String jwt) throws Exception{
		
		EventDTO events=eventService.createEvent(eventRequest);
		
		return new ResponseEntity<>(events, HttpStatus.OK);
	}
	
	@GetMapping("/event/{restaurentId}/restaurent")
	public ResponseEntity<List<EventDTO>> getRestaurentEvents(@PathVariable long restaurentId,  @RequestHeader("Authorization")String jwt) throws Exception
	{

		List<EventDTO>events=eventService.getRestaurentEvents(restaurentId);
		
		return new ResponseEntity<>(events, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/event/{eventId}/delete")
	public ResponseEntity<?> deleteEvenetById(@PathVariable long eventId,  @RequestHeader("Authorization")String jwt) throws Exception
	{
		String message=eventService.deleteEvent(eventId);	
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	@PutMapping("/event/{restaurentId}/update/{eventId}")
	public ResponseEntity<EventDTO> updateEvent(@PathVariable long eventId, @PathVariable long restaurentId, @RequestBody EventDTO eventDTO,  @RequestHeader("Authorization")String jwt) throws Exception{
		EventDTO updatedEvent=eventService.updateEvent(eventId, eventDTO, restaurentId);
		
	    if (updatedEvent != null) {
	        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);  // Return updated event
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Event not found
	    }

	}
	
	@GetMapping("/event/{eventId}")
	public ResponseEntity<EventDTO>getEventById(@PathVariable long eventId, @RequestHeader("Authorization")String jwt ) throws Exception
	{		
		EventDTO events=eventService.findEventById(eventId);
	    if (events != null) {
	        return new ResponseEntity<>(events, HttpStatus.OK);  
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);  
	    }     
	}

}
