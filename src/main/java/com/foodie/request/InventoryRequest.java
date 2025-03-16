package com.foodie.request;

import lombok.Data;

@Data
public class InventoryRequest {

    private String ingredientName;
    private Long ingredientId; // ID of the ingredient
    private int quantity; // The current stock level
    private int lowStockThreshold; // The threshold for low stock
    

}
