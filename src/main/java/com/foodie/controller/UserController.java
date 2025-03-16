package com.foodie.controller;

import com.foodie.dto.AddressDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.dto.UserUpdateDTO;
import com.foodie.request.AddressRequest;
import com.foodie.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServices userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseEntity.ok(userService.getProfile(token));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestHeader("Authorization") String token, 
                                                         @RequestBody UserUpdateDTO userUpdateDTO) throws Exception {
        return ResponseEntity.ok(userService.updateProfile(token, userUpdateDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) throws Exception {
        userService.deleteUser(token);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseEntity.ok(userService.getOrderHistory(token));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getSavedAddresses(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseEntity.ok(userService.getSavedAddresses(token));
    }

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> addAddress(@RequestHeader("Authorization") String token, 
                                                 @RequestBody AddressRequest addressRequest) throws Exception {
        return ResponseEntity.ok(userService.addAddress(token, addressRequest));
    }
}
