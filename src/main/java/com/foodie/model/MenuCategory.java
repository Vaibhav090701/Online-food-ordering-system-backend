package com.foodie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class MenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private String categoryDescription;

    private String categoryImages;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menuCategory")
    private List<MenuItem> menuItems;

    private boolean deleted;
}