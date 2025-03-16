package com.foodie.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.dto.CartDTO;
import com.foodie.dto.CartItemDTO;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.model.Cart;
import com.foodie.model.CartItem;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.User;
import com.foodie.repository.CartRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.CartItemRequest;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserServices userServices;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public CartDTO getCart(String token) throws Exception {
        // Retrieve the logged-in user from the token
    	User user=userServices.findUserByJwtToken(token);

        // Get the user's cart
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        return convertToDTO(cart);
    }

    @Override
    public CartDTO addToCart(String token, CartItemRequest request) throws Exception {
        // Retrieve the logged-in user from the token
    	User user=userServices.findUserByJwtToken(token);

        // Get the menu item
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Get or create a cart
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        
		//to check the food is already present in the cart or not is it is present then just update the quantity
		for(CartItem cartItem:cart.getItems())
		{
			if(cartItem.getMenuItem().equals(menuItem))
			{
				int newQuantity=cartItem.getQuantity()+request.getQuantity();
				return updateCartItem(token, cartItem.getId(), newQuantity);
			}
		}

        // Add the item to the cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(menuItem.getPrice());
        // Handle the ingredients list: if null or empty, set it to an empty list
        List<String> ingredients = (request.getIngredients() == null || request.getIngredients().isEmpty())
                ? new ArrayList<>()
                : request.getIngredients();
        cartItem.setIngredients(ingredients);

        cart.getItems().add(cartItem);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    public CartDTO updateCartItem(String token, Long itemId, int quantity) throws Exception {
    	User user=userServices.findUserByJwtToken(token);

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        double menuItemPrice = cartItem.getMenuItem().getPrice();

        // Calculate the updated price based on the new quantity
        double updatedPrice = menuItemPrice * quantity;

        cartItem.setQuantity(quantity);
        cartItem.setPrice(updatedPrice);  
        
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    public CartDTO removeCartItem(String token, Long itemId) throws Exception {
    	User user=userServices.findUserByJwtToken(token);

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cart.getItems().remove(cartItem);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    public void clearCart(String token) throws Exception {
    	User user=userServices.findUserByJwtToken(token);

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDTO applyCoupon(String token, String couponCode) throws Exception {
        // You can implement the logic to apply the coupon here.
        // For now, we assume there's no actual coupon functionality implemented.

    	User user=userServices.findUserByJwtToken(token);

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        // Apply the coupon logic here

        return convertToDTO(cart);
    }

    // Helper method to convert Cart to CartDTO
    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getItems().stream().mapToDouble(CartItem::getPrice).sum());

        // Convert CartItems to CartItemDTO
        List<CartItemDTO> cartItemDTOs = cart.getItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setMenuItemDto(convertToMenuItemDTO(item.getMenuItem()));
            dto.setName(item.getMenuItem().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setIngredients(item.getIngredients());
            dto.setId(item.getId());
            dto.setRestaurantId(item.getMenuItem().getRestaurant().getId());
           
            return dto;
        }).toList();

        cartDTO.setItems(cartItemDTOs);
        return cartDTO;
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

}
