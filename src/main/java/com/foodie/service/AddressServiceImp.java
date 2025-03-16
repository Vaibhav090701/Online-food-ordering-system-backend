package com.foodie.service;

import com.foodie.dto.AddressDTO;
import com.foodie.model.Address;
import com.foodie.model.User;
import com.foodie.repository.AddressRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.AddressRequest;
import com.foodie.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImp implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServices userServices;

    @Override
    public AddressDTO createAddress(String token, AddressRequest request) throws Exception {
        // Fetch the current user based on the JWT token
        User user = userServices.findUserByJwtToken(token);

        // Create a new Address object
        Address address = new Address();
        address.setStreet(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.isDefault());
        address.setUser(user);

        // Save the address
        addressRepository.save(address);

        // Convert Address to DTO and return
        return convertToDTO(address);
    }

    @Override
    public AddressDTO getAddressById(Long addressId) throws Exception {
        // Fetch the address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Convert Address to DTO and return
        return convertToDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddresses(String token) throws Exception {
        // Fetch the current user based on the JWT token
        User user = userServices.findUserByJwtToken(token);

        // Fetch all addresses for the user
        List<Address> addresses = addressRepository.findByUser(user);

        // Convert list of addresses to DTOs and return
        return addresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressRequest request) throws Exception {
        // Fetch the address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Update the fields of the address
        address.setStreet(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.isDefault());

        // Save the updated address
        addressRepository.save(address);

        // Convert Address to DTO and return
        return convertToDTO(address);
    }

    @Override
    public void deleteAddress(Long addressId) throws Exception {
        // Fetch the address by ID
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Delete the address
        addressRepository.delete(address);
    }


    // Helper method to convert Address entity to AddressDTO
    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreetAddress(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipCode(address.getZipCode());
        dto.setLandmark(address.getLandmark());
        dto.setDefault(address.isDefault());
        return dto;
    }
}
