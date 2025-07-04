package com.foodie.request;

import com.foodie.model.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentToken; // For Stripe payments

    @NotEmpty(message = "Order items are required")
    private List<OrderItemRequest> orderItems;

    // Getters and setters generated by Lombok
}