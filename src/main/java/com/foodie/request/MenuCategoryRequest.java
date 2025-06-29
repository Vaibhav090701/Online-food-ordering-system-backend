package com.foodie.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MenuCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String categoryDescription;
    private String categoryImages;
    private boolean globalCategory;

    // Getters and setters generated by Lombok
}