package com.foodie.service;

import com.foodie.dto.AddressDTO;
import com.foodie.model.Address;
import com.foodie.model.User;
import com.foodie.repository.AddressRepository;
import com.foodie.request.AddressRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImp implements AddressService {

    private final AddressRepository addressRepository;
    private final UserServices userServices;

    @Override
    @Transactional
    public AddressDTO createAddress(String email, AddressRequest request) throws Exception {
        User user = userServices.findUserByEmail(email);

        if (request.isDefault()) {
            List<Address> userAddresses = addressRepository.findByUser(user);
            userAddresses.forEach(addr -> addr.setDefault(false));
            addressRepository.saveAll(userAddresses);
        }

        Address address = new Address();
        address.setStreet(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.isDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    public AddressDTO getAddressById(Long addressId) throws Exception {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        return convertToDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddresses(String email) throws Exception {
        User user = userServices.findUserByEmail(email);
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressRequest request) throws Exception {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (request.isDefault()) {
            User user = address.getUser();
            List<Address> userAddresses = addressRepository.findByUser(user);
            userAddresses.forEach(addr -> {
                if (!addr.getId().equals(addressId)) {
                    addr.setDefault(false);
                }
            });
            addressRepository.saveAll(userAddresses);
        }

        address.setStreet(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.isDefault());

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) throws Exception {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressDTO setIsDefault(Long addressId, String email) throws Exception {
        User user = userServices.findUserByEmail(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (!address.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to modify this address");
        }

        List<Address> userAddresses = addressRepository.findByUser(user);
        userAddresses.forEach(addr -> addr.setDefault(addr.getId().equals(addressId)));
        addressRepository.saveAll(userAddresses);

        return convertToDTO(addressRepository.findById(addressId).get());
    }

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