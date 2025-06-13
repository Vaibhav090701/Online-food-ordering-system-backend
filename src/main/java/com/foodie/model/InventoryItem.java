package com.foodie.model;

import java.util.List;

import com.foodie.dto.IngredientDTO;
import com.foodie.dto.InventoryItemDTO;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    private String ingredientName;
    private int currentStock;
    private double quantity;
    private String unit;
    private int lowStockThreshold;
    
    private boolean deleted;
    
    
    private Ingredients ingredient;

    
    

}
