package com.foodie.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private boolean available = true;
    
    private boolean isVegetarian;
    private String templateType;
    private boolean deleted;
    
    @ManyToOne
    @JoinColumn(name = "menu_category_id")
    private MenuCategory menuCategory;

    
    @Column(length = 1000)
    @ElementCollection
    private List<String> images;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    

    @ManyToMany
    @JoinTable(
        name = "menu_item_ingredients",
        joinColumns = @JoinColumn(name = "menu_item_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredients> ingredients = new ArrayList<>();

    // constructors, getters, setters
}
