package com.foodie.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // To ignore null values during serialization

public class CartItemRequest {
	
	private Long restaurantId;

    private Long menuItemId;
    
    private int quantity;
    
	private List<String> ingredients;


}
