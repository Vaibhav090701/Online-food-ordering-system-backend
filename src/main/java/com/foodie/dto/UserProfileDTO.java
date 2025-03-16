package com.foodie.dto;

import java.util.List;

import com.foodie.model.Role;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private List<RestaurantOwnerDTO>favourites;


}
