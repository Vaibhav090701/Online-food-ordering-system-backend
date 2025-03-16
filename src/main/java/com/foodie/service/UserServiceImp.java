package com.foodie.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.Config.JwtProvider;
import com.foodie.dto.AddressDTO;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.OrderItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.dto.UserUpdateDTO;
import com.foodie.model.Address;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Order;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.UserRepository;
import com.foodie.repository.AddressRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.request.AddressRequest;

@Service
public class UserServiceImp implements UserServices {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public UserProfileDTO getProfile(String token) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = findUserByEmail(email);

        // Map User to UserProfileDTO
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setName(user.getUsername());
        userProfileDTO.setRole(user.getRole());
        userProfileDTO.setFavourites(user.getFavourites().stream().map(this::convertToRestaurantOwnerDTO).collect(Collectors.toList()));
        return userProfileDTO;
    }

    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user;
    }
    

	public User findUserByJwtToken(String jwt) throws Exception {	
		String email= jwtProvider.getEmailFromJwtToken(jwt);
		User user=findUserByEmail(email);
		return user;
	}


    @Override
    public UserProfileDTO updateProfile(String token, UserUpdateDTO updateDto) throws Exception {
//        String email = jwtProvider.getEmailFromJwtToken(token);
//        User user = findUserByEmail(email);
//        
//        user.setUsername(updateDto.getUsername());
//        user.setEmail(updateDto.getEmail());
//        user.setPassword(updateDto.getPassword()); // Ensure password hashing happens in a service method
//        
//        userRepository.save(user);
//        
//        UserProfileDTO userProfileDTO = new UserProfileDTO();
//        userProfileDTO.setEmail(user.getEmail());
//        userProfileDTO.setUsername(user.getUsername());
//        userProfileDTO.setRole(user.getRole());
//        return userProfileDTO;
    	return null;
    }

    @Override
    public void deleteUser(String token) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = findUserByEmail(email);

        userRepository.delete(user);
    }

    @Override
    public List<OrderDTO> getOrderHistory(String token) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = findUserByEmail(email);

        List<Order> orders = orderRepository.findByUser(user);
        List<OrderDTO> orderDTOs = orders.stream().map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderDate(order.getOrderDate());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setItemName(convertToMenuItemDTO(item.getMenuItem()));
                return itemDTO;
            }).collect(Collectors.toList()));
            return orderDTO;
        }).collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new Exception("No orders found for the user.");
        }

        return orderDTOs;
    }

    @Override
    public List<AddressDTO> getSavedAddresses(String token) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = findUserByEmail(email);

        List<AddressDTO> addressDTOs = user.getAddresses().stream().map(address -> {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(address.getId());
            addressDTO.setStreetAddress(address.getStreet());
            addressDTO.setCity(address.getCity());
            addressDTO.setState(address.getState());
            addressDTO.setZipCode(address.getZipCode());
            addressDTO.setLandmark(address.getLandmark());
            addressDTO.setDefault(address.isDefault());
            return addressDTO;
        }).collect(Collectors.toList());

        return addressDTOs;
    }

    @Override
    public AddressDTO addAddress(String token, AddressRequest addressRequest) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = findUserByEmail(email);

        Address address = new Address();
        address.setStreet(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setZipCode(addressRequest.getZipCode());
        address.setLandmark(addressRequest.getLandmark());
        address.setDefault(addressRequest.isDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(savedAddress.getId());
        addressDTO.setStreetAddress(savedAddress.getStreet());
        addressDTO.setCity(savedAddress.getCity());
        addressDTO.setState(savedAddress.getState());
        addressDTO.setZipCode(savedAddress.getZipCode());
        addressDTO.setLandmark(savedAddress.getLandmark());
        addressDTO.setDefault(savedAddress.isDefault());

        return addressDTO;
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
    
    // Helper methods to convert entities to DTOs
    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setStatus(restaurant.isOpen());
        dto.setDescription(restaurant.getDescription());
        dto.setImages(restaurant.getImages());
        
        List<IngredientDTO> dtos=new ArrayList<IngredientDTO>();
        dto.setIngredients(restaurant.getIngredients().stream()
        		.map(this::convertToDTO)
        		.collect(Collectors.toList()));
        
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList()));
        // Add other necessary fields here (like ingredients)
        return dto;
    }
    
    // Method to convert Ingredients object to IngredientDTO
    private IngredientDTO convertToDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }




}
