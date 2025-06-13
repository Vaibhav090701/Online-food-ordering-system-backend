package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.CartDTO;
import com.foodie.request.CartItemRequest;
import com.foodie.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Get the cart
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            CartDTO cart = cartService.getCart(email);
            return ResponseEntity.ok(new ApiResponse<>(cart, "Cart retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Add an item to the cart
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody CartItemRequest request) {
        try {
            CartDTO cart = cartService.addToCart(email, request);
            return ResponseEntity.ok(new ApiResponse<>(cart, "Item added to cart successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update the quantity of a cart item
    @PutMapping("/update/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        try {
            CartDTO cart = cartService.updateCartItem(email, itemId, quantity);
            return ResponseEntity.ok(new ApiResponse<>(cart, "Cart item updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Remove an item from the cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeCartItem(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long itemId) {
        try {
            CartDTO cart = cartService.removeCartItem(email, itemId);
            return ResponseEntity.ok(new ApiResponse<>(cart, "Cart item removed successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Clear all items from the cart
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            cartService.clearCart(email);
            return ResponseEntity.ok(new ApiResponse<>(null, "Cart cleared successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Apply coupon
    @PostMapping("/apply-coupon")
    public ResponseEntity<ApiResponse<CartDTO>> applyCoupon(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestParam String couponCode) {
        try {
            CartDTO cart = cartService.applyCoupon(email, couponCode);
            return ResponseEntity.ok(new ApiResponse<>(cart, "Coupon applied successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}