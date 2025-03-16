package com.foodie.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderItemDTO {
	private long id;
    private MenuItemDTO itemName;
    private int quantity;
    private double price;
    private List<String>images;


}
