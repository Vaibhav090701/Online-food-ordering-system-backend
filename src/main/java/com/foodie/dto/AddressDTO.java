package com.foodie.dto;

import lombok.Data;

@Data
public class AddressDTO {

    private Long id;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String landmark;
    private boolean isDefault;

}
