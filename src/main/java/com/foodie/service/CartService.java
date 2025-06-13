package com.foodie.service;

import com.foodie.dto.CartDTO;
import com.foodie.request.CartItemRequest;

public interface CartService {

	CartDTO getCart(String email) throws Exception;
    CartDTO addToCart(String email, CartItemRequest request) throws Exception;
    CartDTO updateCartItem(String email, Long itemId, int quantity) throws Exception;
    CartDTO removeCartItem(String email, Long itemId) throws Exception;
    void clearCart(String email) throws Exception;
    CartDTO applyCoupon(String email, String couponCode) throws Exception;
}
