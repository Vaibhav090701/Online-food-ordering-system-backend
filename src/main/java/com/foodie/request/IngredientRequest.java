package com.foodie.request;

import lombok.Data;

@Data
public class IngredientRequest {
    private String name;
    private String description;
    private int quantityInStock;
    private String unit;


}
