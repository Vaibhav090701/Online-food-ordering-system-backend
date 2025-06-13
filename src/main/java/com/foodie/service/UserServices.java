package com.foodie.service;

import java.util.List;

import com.foodie.dto.AddressDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.dto.UserUpdateDTO;
import com.foodie.model.User;
import com.foodie.request.AddressRequest;
import com.foodie.request.RegisterRequest;
import com.foodie.request.UpdateProfileRequest;
import com.foodie.request.UpdateSecurityRequest;

import jakarta.validation.Valid;

public interface UserServices {
//    UserProfileDTO getProfile(String token) throws Exception;
//    UserProfileDTO updateProfile(String token, UserUpdateDTO updateDto) throws Exception;
//    void deleteUser(String token) throws Exception;
//    List<OrderDTO> getOrderHistory(String token) throws Exception;
//    List<AddressDTO> getSavedAddresses(String token) throws Exception;
//    AddressDTO addAddress(String token, AddressRequest addressRequest) throws Exception;
    
	public User findUserByJwtToken(String jwt) throws Exception;
	
    public UserProfileDTO createUser(RegisterRequest request);
	UserProfileDTO getUser(String email);
	UserProfileDTO updateProfile(String email, UpdateProfileRequest request) throws Exception;
	void sendOtp(String email);
	void verifyOtp(String email, String otp);
	void sendResetOtp(String email);
	void resetPassword(String email, String otp, String newPassword);
	void updateSecurity(String email, UpdateSecurityRequest request);

	public AddressDTO addAddress(String email, AddressRequest addressRequest);

	public List<OrderDTO> getOrderHistory(String email);

	public void deleteUser(String email);

	public UserProfileDTO getProfile(String email);

	List<AddressDTO> getSavedAddresses(String email) throws Exception;

	public User findUserByEmail(String email);


}
