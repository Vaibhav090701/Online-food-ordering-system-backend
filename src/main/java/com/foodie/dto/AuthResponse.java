package com.foodie.dto;

import com.foodie.model.Role;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private Long id;
    private String email;
    private Role role;
    private String message;
    private String username;
  

}
