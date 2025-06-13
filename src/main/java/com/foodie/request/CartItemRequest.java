package com.foodie.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private List<String> ingredients;

}
