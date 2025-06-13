package com.foodie.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must be less than 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be less than 50 characters")
    private String city;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Size(max = 100, message = "Instagram handle must be less than 100 characters")
    private String instagram;

    @Size(max = 100, message = "Twitter handle must be less than 100 characters")
    private String twitter;

    private boolean status;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotBlank(message = "Cuisine type is required")
    @Size(max = 50, message = "Cuisine type must be less than 50 characters")
    private String cuisineType;

    @NotBlank(message = "Restaurant category is required")
    @Size(max = 50, message = "Restaurant category must be less than 50 characters")
    private String restaurantCategory;

    @NotNull(message = "Images are required")
    @Size(max = 3, message = "Maximum of 3 images allowed")
    private List<String> images;
}