package com.foodie.service;

import com.foodie.dto.CartDTO;
import com.foodie.dto.CartItemDTO;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuCategoryDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.model.Cart;
import com.foodie.model.CartItem;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuCategory;
import com.foodie.model.MenuItem;
import com.foodie.model.Restaurant;
import com.foodie.model.User;
import com.foodie.repository.CartRepository;
import com.foodie.repository.IngredientRepository;
import com.foodie.repository.MenuItemRepository;
import com.foodie.request.CartItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CartRepository cartRepository;
    private final UserServices userServices;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public CartDTO getCart(String email) throws Exception {
        User user = userServices.findUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addToCart(String email, CartItemRequest request) throws Exception {
        User user = userServices.findUserByEmail(email);
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // Check if the item is already in the cart
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getMenuItem().equals(menuItem)) {
                int newQuantity = cartItem.getQuantity() + request.getQuantity();
                return updateCartItem(email, cartItem.getId(), newQuantity);
            }
        }

        // Add new item to cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(menuItem.getPrice() * request.getQuantity());
        List<String> ingredients = (request.getIngredients() == null || request.getIngredients().isEmpty())
                ? new ArrayList<>()
                : request.getIngredients();
        cartItem.setIngredients(ingredients);

        cart.getItems().add(cartItem);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String email, Long itemId, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }

        User user = userServices.findUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        double menuItemPrice = cartItem.getMenuItem().getPrice();
        cartItem.setQuantity(quantity);
        cartItem.setPrice(menuItemPrice * quantity);

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(String email, Long itemId) throws Exception {
        User user = userServices.findUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cart.getItems().remove(cartItem);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(String email) throws Exception {
        User user = userServices.findUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartDTO applyCoupon(String email, String couponCode) throws Exception {
        User user = userServices.findUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        // TODO: Implement coupon logic (e.g., validate couponCode, apply discount)
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Coupon functionality not implemented yet");

        // Example placeholder for future implementation:
        /*
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid coupon code"));
        if (!coupon.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is expired or invalid");
        }
        cart.setDiscount(coupon.getDiscountAmount());
        cartRepository.save(cart);
        */

        // return convertToDTO(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getItems().stream().mapToDouble(CartItem::getPrice).sum());

        List<CartItemDTO> cartItemDTOs = cart.getItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            Restaurant restaurant = item.getMenuItem().getRestaurant();
            List<Ingredients> restaurantIngredients = ingredientRepository.findByRestaurantAndDeletedFalse(restaurant);
            List<String> selectedIngredientNames = item.getIngredients();
            List<IngredientDTO> ingredientDTOs = restaurantIngredients.stream()
                    .filter(ingredient -> selectedIngredientNames.contains(ingredient.getName()))
                    .map(this::convertToIngredientDTO)
                    .toList();
            dto.setIngredients(ingredientDTOs);
            dto.setMenuItemDto(convertToMenuItemDTO(item.getMenuItem()));
            dto.setName(item.getMenuItem().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setId(item.getId());
            dto.setRestaurantId(item.getMenuItem().getRestaurant().getId());
            return dto;
        }).toList();

        cartDTO.setItems(cartItemDTOs);
        return cartDTO;
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

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setVegetarian(menuItem.isVegetarian());
        dto.setImages(menuItem.getImages());
        dto.setCategory(convertToMenuCategoryDTO(menuItem.getMenuCategory()));
        dto.setTemplateType(menuItem.getTemplateType());
        List<IngredientDTO> dtos = menuItem.getIngredients().stream()
                .map(this::convertToIngredientDTO)
                .toList();
        dto.setIngredients(dtos);
        return dto;
    }

    public MenuCategoryDTO convertToMenuCategoryDTO(MenuCategory menuCategory) {
        MenuCategoryDTO dto = new MenuCategoryDTO();
        dto.setCategoryDescription(menuCategory.getCategoryDescription());
        dto.setCategoryImage(menuCategory.getCategoryImages());
        dto.setCategoryName(menuCategory.getCategoryName());
        dto.setId(menuCategory.getId());
        dto.setDeleted(menuCategory.isDeleted());
        return dto;
    }
}