package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.InventoryDTO;
import com.foodie.dto.InventoryItemDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.OrderItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.InventoryItem;
import com.foodie.model.MenuItem;
import com.foodie.model.Order;
import com.foodie.model.OrderItem;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.InventoryRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.RestaurantRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerServiceImp implements RestaurantOwnerService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final InventoryRepository inventoryRepository;
    private final UserServices userServices;

    @Override
    @Transactional
    public RestaurantOwnerDTO createRestaurant(RestaurantRequest request, String email) {
        User user = validateUser(email);

        Restaurant restaurant = new Restaurant();
        populateRestaurantFields(restaurant, request, user);
        restaurant.setDeleted(false);
        restaurant.setOpen(false); // Pending approval

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return convertToRestaurantOwnerDTO(savedRestaurant);
    }

    @Override
    @Transactional
    public RestaurantOwnerDTO updateRestaurantDetails(Long id, RestaurantRequest request, String email) {
        User user = validateUser(email);
        Restaurant restaurant = validateRestaurant(id, user);

        if (request.getImages().size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum of 3 images allowed");
        }

        Set<String> currentImages = new HashSet<>(restaurant.getImages());
        List<String> newImages = request.getImages().stream()
                .filter(image -> !currentImages.contains(image))
                .toList();
        currentImages.addAll(newImages);
        restaurant.setImages(new ArrayList<>(currentImages));

        populateRestaurantFields(restaurant, request, user);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return convertToRestaurantOwnerDTO(savedRestaurant);
    }

    @Override
    public RestaurantOwnerDTO getRestaurantOfUser(String email) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        return convertToRestaurantOwnerDTO(restaurant);
    }

    @Override
    @Transactional
    public RestaurantOwnerDTO updateRestaurantStatus(String email, Long restaurantId) {
        User user = validateUser(email);
        Restaurant restaurant = validateRestaurant(restaurantId, user);

        restaurant.setOpen(!restaurant.isOpen());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return convertToRestaurantOwnerDTO(savedRestaurant);
    }

    @Override
    public List<OrderDTO> getTodayOrders(String email) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        LocalDate today = LocalDate.now();
        List<Order> orders = orderRepository.findByRestaurantAndOrderDateBetweenAndDeletedFalse(
                restaurant, today.atStartOfDay(), today.atTime(23, 59, 59));

        return orders.stream()
                .map(this::convertToOrderDTO)
                .toList();
    }

    @Override
    @Transactional
    public MenuItemDTO toggleMenuItemAvailability(String email, Long itemId) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));

        if (!menuItem.getRestaurant().equals(restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu item does not belong to your restaurant");
        }

        menuItem.setAvailable(!menuItem.isAvailable());
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToMenuItemDTO(savedMenuItem);
    }

    @Override
    public InventoryDTO getInventoryStatus(String email) {
        User user = validateUser(email);
        Restaurant restaurant = restaurantRepository.findByOwnerAndDeletedFalse(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        List<InventoryItem> inventoryItems = inventoryRepository.findByRestaurantAndDeletedFalse(restaurant);

        InventoryDTO dto = new InventoryDTO();
        List<InventoryItemDTO> itemDTOs = inventoryItems.stream()
                .map(this::convertToInventoryItemDTO)
                .toList();
        dto.setItems(itemDTOs);
        return dto;
    }

    private User validateUser(String email) {
        User user = userServices.findUserByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private Restaurant validateRestaurant(Long id, User user) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        if (!restaurant.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to modify this restaurant");
        }
        return restaurant;
    }

    private void populateRestaurantFields(Restaurant restaurant, RestaurantRequest request, User user) {
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setName(request.getName());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setInstagram(request.getInstagram());
        restaurant.setTwitter(request.getTwitter());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setRestaurantCategory(request.getRestaurantCategory());
        restaurant.setOwner(user);
    }

    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setCity(restaurant.getCity());
        dto.setPhone(restaurant.getPhone());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setRestaurantCategory(restaurant.getRestaurantCategory());
        dto.setEmail(restaurant.getEmail());
        dto.setInstagram(restaurant.getInstagram());
        dto.setTwitter(restaurant.getTwitter());
        dto.setStatus(restaurant.isOpen());
        dto.setDeleted(restaurant.isDeleted());
        dto.setImages(restaurant.getImages());
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .filter(item -> !item.isDeleted())
                .map(this::convertToMenuItemDTO)
                .toList());
        dto.setIngredients(restaurant.getIngredients().stream()
                .filter(ingredient -> !ingredient.isDeleted())
                .map(this::convertToIngredientDTO)
                .toList());
        return dto;
    }

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImages(menuItem.getImages());
        dto.setDeleted(menuItem.isDeleted());
        dto.setIngredients(menuItem.getIngredients().stream()
                .filter(ingredient -> !ingredient.isDeleted())
                .map(this::convertToIngredientDTO)
                .toList());
        return dto;
    }

    private IngredientDTO convertToIngredientDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        dto.setPrice(ingredient.getPrice());
        dto.setDeleted(ingredient.isDeleted());
        return dto;
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeleted(order.isDeleted());
        dto.setItems(order.getItems().stream()
                .filter(item -> !item.isDeleted())
                .map(this::convertToOrderItemDTO)
                .toList());
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setItemName(convertToMenuItemDTO(orderItem.getMenuItem()));
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setDeleted(orderItem.isDeleted());
        return dto;
    }

    private InventoryItemDTO convertToInventoryItemDTO(InventoryItem inventoryItem) {
        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setIngredient(convertToIngredientDTO(inventoryItem.getIngredient()));
        dto.setQuantity(inventoryItem.getQuantity());
        dto.setUnit(inventoryItem.getIngredient().getUnit());
        dto.setLowStockThreshold(inventoryItem.getLowStockThreshold());
        dto.setDeleted(inventoryItem.isDeleted());
        return dto;
    }
}