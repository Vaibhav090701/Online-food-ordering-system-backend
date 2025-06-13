package com.foodie.request;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class IngredientRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity in stock is required")
    @Min(value = 0, message = "Quantity in stock must be non-negative")
    private Integer quantityInStock;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

}
