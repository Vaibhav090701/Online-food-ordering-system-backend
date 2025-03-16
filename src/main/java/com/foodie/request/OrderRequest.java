package com.foodie.request;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
	
    private String specialInstructions;
    private String paymentMethod;  // "CARD", "CASH", etc.
    private Long addressId;  // Include the addressId to link an address to the order
    private List<OrderItemRequest> orderItems;  // List of order items
    private Long restaurantId;


    

}
