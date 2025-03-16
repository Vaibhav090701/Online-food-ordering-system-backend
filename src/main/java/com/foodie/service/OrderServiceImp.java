package com.foodie.service;


import com.foodie.dto.AddressDTO;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.OrderItemDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.model.Address;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Order;
import com.foodie.model.OrderItem;
import com.foodie.model.OrderStatus;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.UserRepository;
import com.foodie.repository.AddressRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.OrderItemRequest;
import com.foodie.request.OrderRequest;
import com.foodie.service.OrderService;
import com.foodie.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserServices userServices;
    
    @Autowired
    private AddressRepository addressRepository; // Add this


    @Override
    public OrderDTO placeOrder(String token, OrderRequest request) throws Exception {
        // Fetch the current user based on the JWT token
        User user = userServices.findUserByJwtToken(token);
        
        // Assume that the restaurant is associated with the user (in case of a customer order)
        Restaurant restaurant =restaurantRepository.findById(request.getRestaurantId()).orElse(null);
        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found for the user.");
        }
        
        // Fetch the address by ID
        //Later on in frontend we need to give options like Home, Work,etc to find address ID
        //because one user have multiple address. We need to provide addressId from frontend/
        Address deliveryAddress = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Create a new order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);  // The total will be calculated based on items
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);


        // Create order items from the request (assuming you have order items in the request)
        List<OrderItem> orderItems = createOrderItemsFromRequest(request.getOrderItems(), order);
        order.setItems(orderItems);

        // Calculate total amount based on the order items
        double totalAmount = calculateTotalAmount(orderItems);
        order.setTotalAmount(totalAmount);

        // Save the order
        orderRepository.save(order);

        // Convert to DTO and return
        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderDetails(String token, Long orderId) {
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Ensure the order belongs to the user
        if (!order.getUser().equals(user)) {
            throw new RuntimeException("This order does not belong to the user");
        }

        // Convert to DTO and return
        return convertToDTO(order);
    }

    @Override
    public List<OrderDTO> getOrderHistory(String token) {
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Fetch orders for the user
        List<Order> orders = orderRepository.findByUser(user);
        
        // Convert list of orders to DTOs and return
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public OrderDTO cancelOrder(String token, Long orderId) {
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Ensure the order belongs to the user
        if (!order.getUser().equals(user)) {
            throw new RuntimeException("This order does not belong to the user");
        }

        // Update the order status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Convert to DTO and return
        return convertToDTO(order);
    }

    @Override
    public Page<OrderDTO> getRestaurantOrders(String jwt, String status, int page, int size) {
        User user = null;
		try {
			user = userServices.findUserByJwtToken(jwt);
	        System.out.println("User fetched: " + user);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Fetch the restaurant of the user (assuming user is the restaurant owner)
        Restaurant restaurant =restaurantRepository.findByOwner(user);
        
        // Define pageable object with page number and size
        Pageable pageable=PageRequest.of(page, size);
        
        // If status is provided, filter by status
        Page<Order> orders;
        
        if ("ALL".equals(status)) {
            System.out.println("Fetching all orders for restaurant");
            orders = orderRepository.findByRestaurant(restaurant, pageable);
        } else {
            try {

                OrderStatus orderStatus = OrderStatus.valueOf(status); // Convert string to OrderStatus enum
                System.out.println("Filtering orders with status: " + orderStatus);

                orders = orderRepository.findByRestaurantAndStatus(restaurant, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                // If status is not a valid enum, return an empty page or handle accordingly
                orders = Page.empty(pageable);
            }
        }
        System.out.println("Total orders fetched: " + orders.getTotalElements()); // Check if total elements are greater than 0

                
        // Convert list of orders to DTOs and return
        return orders.map(this::convertToDTO);
    } 

    @Override
    public OrderDTO updateOrderStatus(String token, Long orderId, String status) {
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        Restaurant restaurant =restaurantRepository.findByOwner(user);

        // Ensure the order belongs to the restaurant of the user (assuming the user is a restaurant owner)
        if (!order.getRestaurant().equals(restaurant)) {
            throw new RuntimeException("This order does not belong to the restaurant");
        }

        OrderStatus status1 = OrderStatus.valueOf(status);  // This will throw IllegalArgumentException if the string is invalid

        // Update the order status
        order.setStatus(status1);
        orderRepository.save(order);

        // Convert to DTO and return
        return convertToDTO(order);
    }

    // Helper methods to calculate total amount and convert entities to DTOs

    private double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setItems(order.getItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        Address address=order.getDeliveryAddress();
        AddressDTO addressDTO=new AddressDTO();
        addressDTO.setCity(address.getCity());
        addressDTO.setId(address.getId());
        addressDTO.setLandmark(address.getLandmark());
        addressDTO.setState(address.getState());
        addressDTO.setStreetAddress(address.getStreet());
        addressDTO.setZipCode(address.getZipCode());
        
        orderDTO.setUserProfileDTO(convertToUserProfileDTO(order.getUser()));
        
        orderDTO.setDeliveryAddress(addressDTO);
        return orderDTO;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setItemName(convertToMenuItemDTO(orderItem.getMenuItem()));
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }
    
    private UserProfileDTO convertToUserProfileDTO(User user) {
    	
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setName(user.getUsername());
        userProfileDTO.setRole(user.getRole());
        
        return userProfileDTO;
    }
    
    // Helper methods to convert entities to DTOs
    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImages(menuItem.getImages());
        List<IngredientDTO>dtos=new ArrayList<IngredientDTO>();
        for(Ingredients ingredients:menuItem.getIngredients()) {
        	IngredientDTO dto1=new IngredientDTO();
        	dto1.setDescription(ingredients.getDescription());
        	dto1.setName(ingredients.getName());
        	dto1.setId(ingredients.getId());
        	dto1.setQuantityInStock(ingredients.getQuantityInStock());
        	dto1.setUnit(ingredients.getUnit());
        	
        	dtos.add(dto1);
        }
        
        dto.setIngredients(dtos);
        return dto;
    }



    private List<OrderItem> createOrderItemsFromRequest(List<OrderItemRequest> orderItemRequests, Order order) {
        return orderItemRequests.stream().map(itemRequest -> {
            // Fetch the menu item based on the ID
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            // Create and populate OrderItem entity
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());  // Assuming MenuItem has a price field

            return orderItem;
        }).collect(Collectors.toList());
    }

}
