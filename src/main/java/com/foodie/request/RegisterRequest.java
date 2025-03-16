package com.foodie.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    
    private String email;
    
    private String password;
    
    private String role;  // "CUSTOMER", "RESTAURANT_OWNER"
    
    // Getters & Setters
}
