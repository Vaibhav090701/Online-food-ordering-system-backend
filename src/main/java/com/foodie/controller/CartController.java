package com.foodie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodie.dto.CartDTO;
import com.foodie.request.CartItemRequest;
import com.foodie.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Get the cart
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestHeader("Authorization") String token) throws Exception {
        CartDTO cart = cartService.getCart(token);
        return ResponseEntity.ok(cart);
    }

    // Add an item to the cart
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestHeader("Authorization") String token, @RequestBody CartItemRequest request) throws Exception {
    	System.out.println(request);
        CartDTO cart = cartService.addToCart(token, request);
        return ResponseEntity.ok(cart);
    }

    // Update the quantity of a cart item
    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(@RequestHeader("Authorization") String token, @PathVariable Long itemId, @RequestParam int quantity) throws Exception {
        CartDTO cart = cartService.updateCartItem(token, itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    // Remove an item from the cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartDTO> removeCartItem(@RequestHeader("Authorization") String token, @PathVariable Long itemId) throws Exception {
        CartDTO cart = cartService.removeCartItem(token, itemId);
        return ResponseEntity.ok(cart);
    }

    // Clear all items from the cart
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) throws Exception {
        cartService.clearCart(token);
        return ResponseEntity.noContent().build();
    }

    // Apply coupon
    @PostMapping("/apply-coupon")
    public ResponseEntity<CartDTO> applyCoupon(@RequestHeader("Authorization") String token, @RequestParam String couponCode) throws Exception {
        CartDTO cart = cartService.applyCoupon(token, couponCode);
        return ResponseEntity.ok(cart);
    }
}
