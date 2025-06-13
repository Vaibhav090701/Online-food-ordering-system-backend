package com.foodie.dto;

import lombok.Data;

@Data
public class IngredientDTO {
	    private Long id;
	    private String name;
	    private String description;
	    private int quantityInStock;
	    private String unit;
	    private double price;
	    private boolean deleted;

}
