package com.foodie.controller;

import com.foodie.dto.AddressDTO;
import com.foodie.dto.ApiResponse;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.request.AddressRequest;
import com.foodie.request.RegisterRequest;
import com.foodie.request.ResetPasswordRequest;
import com.foodie.request.UpdateProfileRequest;
import com.foodie.request.UpdateSecurityRequest;
import com.foodie.service.EmailService;
import com.foodie.service.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserProfileDTO>> createUser(@Valid @RequestBody RegisterRequest request) {
        try {
            UserProfileDTO dto = userService.createUser(request);
            emailService.sendWelcomeMail(request.getEmail(), request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(dto, "User created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUser(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            UserProfileDTO dto = userService.getUser(email);
            return ResponseEntity.ok(new ApiResponse<>(dto, "User retrieved successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            UserProfileDTO dto = userService.getProfile(email);
            return ResponseEntity.ok(new ApiResponse<>(dto, "Profile retrieved successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            userService.updateProfile(email, request);
            return ResponseEntity.ok(new ApiResponse<>(null, "Profile updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok(new ApiResponse<>(null, "User deleted successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrderHistory(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<OrderDTO> orders = userService.getOrderHistory(email);
            return ResponseEntity.ok(new ApiResponse<>(orders, "Order history retrieved successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/address")
    public ResponseEntity<ApiResponse<AddressDTO>> addAddress(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody AddressRequest addressRequest) {
        try {
            AddressDTO address = userService.addAddress(email, addressRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(address, "Address added successfully", HttpStatus.CREATED.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PatchMapping("/security")
    public ResponseEntity<ApiResponse<String>> updateSecurity(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody UpdateSecurityRequest request) {
        try {
            userService.updateSecurity(email, request);
            return ResponseEntity.ok(new ApiResponse<>(null, "Security settings updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            userService.sendOtp(email);
            return ResponseEntity.ok(new ApiResponse<>(null, "OTP sent successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestBody String otp) {
        try {
            userService.verifyOtp(email, otp);
            return ResponseEntity.ok(new ApiResponse<>(null, "OTP verified successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse<>(null, "Password reset successfully", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}