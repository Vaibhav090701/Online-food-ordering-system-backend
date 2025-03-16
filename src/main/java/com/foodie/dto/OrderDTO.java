package com.foodie.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.foodie.model.OrderStatus;

import lombok.Data;

@Data
public class OrderDTO {

    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;
    private List<OrderItemDTO> items;
    private AddressDTO deliveryAddress;  // Add deliveryAddress to the DTO
    private UserProfileDTO userProfileDTO;
  

}
