package com.foodie.request;

import java.util.List;

import lombok.Data;

@Data
public class MenuItemRequest {
	
	    private String name;
	    private String description;
	    private double price;
	    private boolean available=true;
	    private Boolean isVegetarian;
	    private List<String>images;
	    private List<Long> ingredientIds;  // IDs of ingredients used in this item

}
