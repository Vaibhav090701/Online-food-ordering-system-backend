package com.foodie.controller;

import com.foodie.dto.AddressDTO;
import com.foodie.dto.ApiResponse;
import com.foodie.request.AddressRequest;
import com.foodie.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Create a new address
    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressService.createAddress(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(addressDTO, "Address created successfully", HttpStatus.CREATED.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get address details by ID
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(@PathVariable Long addressId) {
        try {
            AddressDTO addressDTO = addressService.getAddressById(addressId);
            return ResponseEntity.ok(new ApiResponse<>(addressDTO, "Address retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get all addresses of the current user
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAllAddresses(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<AddressDTO> addressDTOList = addressService.getAllAddresses(email);
            return ResponseEntity.ok(new ApiResponse<>(addressDTOList, "Addresses retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update an address by ID
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressService.updateAddress(addressId, request);
            return ResponseEntity.ok(new ApiResponse<>(addressDTO, "Address updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Delete an address by ID
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long addressId) {
        try {
            addressService.deleteAddress(addressId);
            return ResponseEntity.ok(new ApiResponse<>(null, "Address deleted successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressDTO>> setAddressIsDefault(
            @PathVariable Long addressId,
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            AddressDTO addressDTO = addressService.setIsDefault(addressId, email);
            return ResponseEntity.ok(new ApiResponse<>(addressDTO, "Default address set successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}