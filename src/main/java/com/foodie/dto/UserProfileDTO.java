package com.foodie.dto;

import java.util.List;

import com.foodie.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String userId;
    private boolean isAccountVerified;
    private Role role;
    private List<RestaurantOwnerDTO>favourites;


}
