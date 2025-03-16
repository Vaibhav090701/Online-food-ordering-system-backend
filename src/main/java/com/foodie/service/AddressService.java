package com.foodie.service;

import com.foodie.dto.AddressDTO;
import com.foodie.request.AddressRequest;

import java.util.List;

public interface AddressService {

    AddressDTO createAddress(String token, AddressRequest request) throws Exception;

    AddressDTO getAddressById(Long addressId) throws Exception;

    List<AddressDTO> getAllAddresses(String token) throws Exception;

    AddressDTO updateAddress(Long addressId, AddressRequest request) throws Exception;

    void deleteAddress(Long addressId) throws Exception;

}
