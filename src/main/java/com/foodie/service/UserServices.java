package com.foodie.service;

import java.util.List;

import com.foodie.dto.AddressDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.dto.UserUpdateDTO;
import com.foodie.model.User;
import com.foodie.request.AddressRequest;

public interface UserServices {
    UserProfileDTO getProfile(String token) throws Exception;
    UserProfileDTO updateProfile(String token, UserUpdateDTO updateDto) throws Exception;
    void deleteUser(String token) throws Exception;
    List<OrderDTO> getOrderHistory(String token) throws Exception;
    List<AddressDTO> getSavedAddresses(String token) throws Exception;
    AddressDTO addAddress(String token, AddressRequest addressRequest) throws Exception;
    
	public User findUserByJwtToken(String jwt) throws Exception;

}
