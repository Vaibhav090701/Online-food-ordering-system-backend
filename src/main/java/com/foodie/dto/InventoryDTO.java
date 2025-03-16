package com.foodie.dto;

import lombok.Data;

@Data
public class InventoryDTO {

    private String ingredientName;
    private int currentStock;
    private String unit;
    private int lowStockThreshold;
    

}
