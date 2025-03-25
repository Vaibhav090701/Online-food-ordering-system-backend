package com.foodie.service;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.InventoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.OrderItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.model.Ingredients;
import com.foodie.model.Inventory;
import com.foodie.model.MenuItem;
import com.foodie.model.Order;
import com.foodie.model.OrderItem;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.InventoryRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.request.RestaurentRequest;
import com.foodie.service.RestaurantOwnerService;
import com.foodie.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RestaurantOwnerServiceImp implements RestaurantOwnerService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserServices userServices;
    
	public RestaurantOwnerDTO createRestaurent(RestaurentRequest req, String token) {
		
		User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Restaurant restaurent=new Restaurant();
		restaurent.setAddress(req.getAddress());
		restaurent.setName(req.getName());
		restaurent.setPhone(req.getPhone());
		restaurent.setEmail(req.getEmail());
		restaurent.setInstagram(req.getInstagram());
		restaurent.setTwitter(req.getTwitter());
		restaurent.setOpen(req.isStatus());
		restaurent.setDescription(req.getDescription());
		restaurent.setImages(req.getImages());
		restaurent.setOwner(user);
		
		Restaurant restaurant= restaurantRepository.save(restaurent);
		
		return convertToRestaurantOwnerDTO(restaurant);
	}


    
    @Override
    public RestaurantOwnerDTO updateRestaurantStatus(String token, long restaurantId) {
        // Fetch the user and update restaurant status
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        restaurant.setOpen(!restaurant.isOpen());
        restaurantRepository.save(restaurant);

        // Convert restaurant entity to DTO and return
        return convertToRestaurantOwnerDTO(restaurant);
    }

    @Override
    public List<OrderDTO> getTodayOrders(String token) {
        // Fetch orders for the restaurant for the current date
//        User user = null;
//		try {
//			user = userServices.findUserByJwtToken(token);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        Restaurant restaurant = restaurantRepository.findByOwner(user);
//
//        LocalDate today = LocalDate.now();
//        List<Order> orders = orderRepository.findByRestaurantAndOrderDate(restaurant, today.atStartOfDay(), today.atTime(23, 59, 59));
//
//        return orders.stream()
//                .map(this::convertToOrderDTO)
//                .collect(Collectors.toList());
    	return null;
    }

    @Override
    public MenuItemDTO toggleMenuItemAvailability(String token, Long itemId) {
        // Toggle availability of menu item
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Restaurant restaurant = restaurantRepository.findByOwner(user);

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        // Check if the item belongs to the restaurant
        if (!menuItem.getRestaurant().equals(restaurant)) {
            throw new RuntimeException("This menu item does not belong to your restaurant");
        }

        menuItem.setAvailable(!menuItem.isAvailable());
        menuItemRepository.save(menuItem);

        // Convert to DTO and return
        return convertToMenuItemDTO(menuItem);
    }

    @Override
    public InventoryDTO getInventoryStatus(String token) {
        // Fetch the user and retrieve inventory status
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Restaurant restaurant = restaurantRepository.findByOwner(user);

        // Assuming inventory is related to ingredients in the restaurant
        List<Inventory> inventoryList = inventoryRepository.findByRestaurant(restaurant);

        // Aggregate and return inventory status as DTO
        InventoryDTO dto = new InventoryDTO();
        Inventory ingredientInventory = inventoryList.get(0);  // Just for example, pick the first inventory item.
        dto.setIngredientName(ingredientInventory.getIngredient().getName());
        dto.setCurrentStock(ingredientInventory.getQuantity());
        dto.setUnit(ingredientInventory.getIngredient().getUnit());
        dto.setLowStockThreshold(ingredientInventory.getLowStockThreshold());

        return dto;
    }

    // Helper methods to convert entities to DTOs
    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setDescription(restaurant.getDescription());
        dto.setEmail(restaurant.getEmail());
        dto.setInstagram(restaurant.getInstagram());
        dto.setTwitter(restaurant.getTwitter());
        dto.setStatus(restaurant.isOpen());
        // Set menuItems and ingredients
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList()));
        dto.setImages(restaurant.getImages());       
        return dto;
    }


    private IngredientDTO convertToIngredientDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setItems(order.getItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setItemName(convertToMenuItemDTO(orderItem.getMenuItem()));
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
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




	@Override
	public RestaurantOwnerDTO getRestaurantOfUser(String token) {
        // Fetch user by JWT token and retrieve restaurant data
        User user = null;
		try {
			user = userServices.findUserByJwtToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Restaurant restaurant = restaurantRepository.findByOwner(user);

        // Collect restaurant details
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setStatus(restaurant.isOpen());
        dto.setDescription(restaurant.getDescription());
        dto.setEmail(restaurant.getEmail());
        dto.setInstagram(restaurant.getInstagram());
        dto.setTwitter(restaurant.getTwitter());
        // Fetch menu items
        List<MenuItemDTO> menuItems = restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList());
        dto.setMenuItems(menuItems);

        // Fetch ingredients
        List<IngredientDTO> ingredients = restaurant.getIngredients().stream()
                .map(this::convertToIngredientDTO)
                .collect(Collectors.toList());
        dto.setIngredients(ingredients);
        
        dto.setImages(restaurant.getImages());

        return dto;
    }



	@Override
	public RestaurantOwnerDTO updateRestaurantDetails(long id, RestaurentRequest req, String jwt) {
		// TODO Auto-generated method stub
        // Fetch user by JWT token and retrieve restaurant data
        User user = null;
		try {
			user = userServices.findUserByJwtToken(jwt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        
	    if (req.getImages().size() > 3) {
	        throw new IllegalArgumentException("You can only upload a maximum of 3 images.");
	    }
        
		restaurant.setAddress(req.getAddress());
		restaurant.setName(req.getName());
		restaurant.setPhone(req.getPhone());
		restaurant.setEmail(req.getEmail());
		restaurant.setInstagram(req.getInstagram());
		restaurant.setTwitter(req.getTwitter());
		restaurant.setDescription(req.getDescription());
		restaurant.setOwner(user);
		
		// Get the current images of the restaurant
	    Set<String> currentImages = new HashSet<>(restaurant.getImages());
	    
	    List<String> newImage=req.getImages().stream()
	    		.filter(image -> !currentImages.contains(image))
	    		.collect(Collectors.toList());
	    
	    // Add the new unique images to the restaurant's existing images
	    currentImages.addAll(newImage);
		
	    // Update the restaurant's images with the new set (still checking duplicates)
	    restaurant.setImages(new ArrayList<>(currentImages));
		
		Restaurant restaurant1= restaurantRepository.save(restaurant);
		
		return convertToRestaurantOwnerDTO(restaurant1);
	}
}
