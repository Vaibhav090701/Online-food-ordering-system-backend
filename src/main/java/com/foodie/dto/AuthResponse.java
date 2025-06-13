package com.foodie.dto;

import com.foodie.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    public AuthResponse(String email2, String jwtToken, String string, String name) {
		// TODO Auto-generated constructor stub
	}
	private String token;
    private Long id;
    private String email;
    private Role role;
    private String message;
    private String username;
  

}
