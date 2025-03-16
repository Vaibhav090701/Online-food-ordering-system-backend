package com.foodie.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodie.dto.UserProfileDTO;
import com.foodie.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Endpoint to get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        List<UserProfileDTO> users = adminService.getAllUsers(token);
        
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // No users found, send 204
        }
        
        return new ResponseEntity<>(users, HttpStatus.OK);  // Successful response, send 200
    }

    // Endpoint to update user role
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserProfileDTO> updateUserRole(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestParam String newRole) {
        
        try {
            UserProfileDTO updatedUser = adminService.updateUserRole(token, userId, newRole);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);  // Send 200 OK if update is successful
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Send 400 if there is an error (e.g., invalid role)
        }
    }

    // More endpoints...
}
