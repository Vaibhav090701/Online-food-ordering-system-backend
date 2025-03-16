package com.foodie.controller;

import com.foodie.dto.AddressDTO;
import com.foodie.request.AddressRequest;
import com.foodie.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Create a new address
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @RequestHeader("Authorization") String token,
            @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressService.createAddress(token, request);
            return new ResponseEntity<>(addressDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get address details by ID
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        try {
            AddressDTO addressDTO = addressService.getAddressById(addressId);
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all addresses of the current user
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAllAddresses(@RequestHeader("Authorization") String token) {
        try {
            List<AddressDTO> addressDTOList = addressService.getAllAddresses(token);
            return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update an address by ID
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequest request) {
        try {
            AddressDTO addressDTO = addressService.updateAddress(addressId, request);
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete an address by ID
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        try {
            addressService.deleteAddress(addressId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
