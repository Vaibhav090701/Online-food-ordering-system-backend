package com.foodie.controller;


import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.request.MenuItemRequest;
import com.foodie.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // Create a new menu item
    @PostMapping
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @RequestBody MenuItemRequest request,            
            @RequestHeader("Authorization") String token) throws Exception {
    	System.out.println(request);
        MenuItemDTO menuItemDTO = menuService.createMenuItem(request, token);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
    


    // Update an existing menu item
    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId,
            @RequestBody MenuItemRequest request) throws Exception {
        MenuItemDTO menuItemDTO = menuService.updateMenuItem(token, itemId, request);
        return new ResponseEntity<>(menuItemDTO, HttpStatus.OK);
    }

    // Delete a menu item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId) throws Exception {
        menuService.deleteMenuItem(token, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get all ingredients
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDTO>> getIngredients(@RequestHeader("Authorization") String token) throws Exception {
        List<IngredientDTO> ingredients = menuService.getIngredients(token);
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    // Update ingredient stock
    @PutMapping("/ingredients/{ingredientId}")
    public ResponseEntity<IngredientDTO> updateIngredientStock(
            @RequestHeader("Authorization") String token,
            @PathVariable Long ingredientId,
            @RequestParam int quantity) throws Exception {
        IngredientDTO ingredientDTO = menuService.updateIngredientStock(token, ingredientId, quantity);
        return new ResponseEntity<>(ingredientDTO, HttpStatus.OK);
    }
}
