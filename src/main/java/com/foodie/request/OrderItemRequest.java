package com.foodie.request;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long menuItemId;  // MenuItem ID
    private int quantity;  // Quantity of the item

}
