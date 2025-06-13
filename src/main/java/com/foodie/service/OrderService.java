package com.foodie.service;

import com.foodie.dto.OrderDTO;
import com.foodie.request.OrderRequest;
import com.stripe.exception.StripeException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(String email, OrderRequest request) throws StripeException;
    OrderDTO getOrderDetails(String email, Long orderId);
    List<OrderDTO> getOrderHistory(String email);
    OrderDTO cancelOrder(String email, Long orderId);
    Page<OrderDTO> getRestaurantOrders(String email, String status, int page, int size);
    OrderDTO updateOrderStatus(String email, Long orderId, String status);
}