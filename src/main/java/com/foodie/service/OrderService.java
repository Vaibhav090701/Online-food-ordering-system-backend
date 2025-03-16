package com.foodie.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.foodie.dto.OrderDTO;
import com.foodie.model.OrderStatus;
import com.foodie.request.OrderRequest;

public interface OrderService {
	
    OrderDTO placeOrder(String token, OrderRequest request) throws Exception;
    OrderDTO getOrderDetails(String token, Long orderId);
    List<OrderDTO> getOrderHistory(String token);
    OrderDTO cancelOrder(String token, Long orderId);
    
    // Restaurant Owner-only
    Page<OrderDTO> getRestaurantOrders(String token, String status, int page, int size);
    OrderDTO updateOrderStatus(String token, Long orderId, String status);


}
