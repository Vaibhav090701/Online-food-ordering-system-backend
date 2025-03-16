package com.foodie.service;

import com.foodie.dto.CartDTO;
import com.foodie.request.CartItemRequest;

public interface CartService {

    CartDTO getCart(String token)throws Exception;
    CartDTO addToCart(String token, CartItemRequest request)throws Exception;
    CartDTO updateCartItem(String token, Long itemId, int quantity)throws Exception;
    CartDTO removeCartItem(String token, Long itemId)throws Exception;
    void clearCart(String token)throws Exception;
    CartDTO applyCoupon(String token, String couponCode) throws Exception;

}
