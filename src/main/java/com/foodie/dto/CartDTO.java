package com.foodie.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private double totalPrice;
    private List<String>images;


}
