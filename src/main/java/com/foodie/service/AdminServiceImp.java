package com.foodie.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.dto.UserProfileDTO;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.UserRepository;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserProfileDTO> getAllUsers(String token) {
        // Here you might want to validate the token or perform authorization checks
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(user.getId());
            dto.setName(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            return dto;
        }).toList();
    }

    @Override
    public UserProfileDTO updateUserRole(String token, Long userId, String newRole) {
        // Validate the token or authorize the admin to perform this action
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            userRepository.save(user);

            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(user.getId());
            dto.setName(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());

            return dto;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role");
        }
    }

    // Implement other methods like getAuditLogs, generateSalesReport, etc.
}
