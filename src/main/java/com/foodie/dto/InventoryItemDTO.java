package com.foodie.dto;

import lombok.Data;

@Data
public class InventoryItemDTO {
    private IngredientDTO ingredient;
    private double quantity;
    private String unit;
    private double lowStockThreshold;
    private boolean deleted;
}