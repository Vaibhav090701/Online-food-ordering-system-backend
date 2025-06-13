package com.foodie.service;

import com.foodie.dto.*;
import com.foodie.model.*;
import com.foodie.repository.*;
import com.foodie.request.OrderItemRequest;
import com.foodie.request.OrderRequest;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserServices userServices;
    private final AddressRepository addressRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, OrderRequest request) throws StripeException {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        Address deliveryAddress = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        if (!deliveryAddress.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Address does not belong to the user");
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(request.getPaymentMethod());

        List<OrderItem> orderItems = createOrderItemsFromRequest(request.getOrderItems(), order, restaurant);
        order.setItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));

        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderDetails(String email, Long orderId) {
        User user = validateUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order does not belong to the user");
        }
        return convertToDTO(order);
    }

    @Override
    public List<OrderDTO> getOrderHistory(String email) {
        User user = validateUser(email);
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(String email, Long orderId) {
        User user = validateUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order does not belong to the user");
        }
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    public Page<OrderDTO> getRestaurantOrders(String email, String status, int page, int size) {
        User user = validateAdminUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found for the user"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if ("ALL".equals(status)) {
            orders = orderRepository.findByRestaurant(restaurant, pageable);
        } else {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status);
                orders = orderRepository.findByRestaurantAndStatus(restaurant, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
            }
        }

        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(String email, Long orderId, String status) {
        User user = validateAdminUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found for the user"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order does not belong to the restaurant");
        }

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            order.setStatus(orderStatus);
            Order savedOrder = orderRepository.save(order);
            return convertToDTO(savedOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
        }
    }

    private User validateUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private User validateAdminUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: User is not an admin");
        }
        return user;
    }

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
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setPaymentStatus(order.getPaymentStatus());
        orderDTO.setItems(order.getItems().stream()
                .map(this::convertToOrderItemDTO)
                .toList());
        orderDTO.setDeliveryAddress(convertToAddressDTO(order.getDeliveryAddress()));
        orderDTO.setUserProfileDTO(convertToUserProfileDTO(order.getUser()));
        return orderDTO;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setStreetAddress(address.getStreet());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setZipCode(address.getZipCode());
        addressDTO.setLandmark(address.getLandmark());
        return addressDTO;
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

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImages(menuItem.getImages());
        List<IngredientDTO> ingredientDTOs = menuItem.getIngredients().stream()
                .map(this::convertToIngredientDTO)
                .toList();
        dto.setIngredients(ingredientDTOs);
        return dto;
    }

    private IngredientDTO convertToIngredientDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        dto.setPrice(ingredient.getPrice());
        return dto;
    }

    private List<OrderItem> createOrderItemsFromRequest(List<OrderItemRequest> orderItemRequests, Order order, Restaurant restaurant) {
        return orderItemRequests.stream().map(itemRequest -> {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
            if (!menuItem.getRestaurant().equals(restaurant)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item does not belong to the specified restaurant");
            }
            if (!menuItem.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item is not available");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            return orderItem;
        }).toList();
    }
}