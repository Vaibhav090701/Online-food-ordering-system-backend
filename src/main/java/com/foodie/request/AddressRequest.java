package com.foodie.request;

import lombok.Data;

@Data
public class AddressRequest {
    private String streetAddress;

    private String city;

    private String state;

    private String zipCode;

    private String landmark;
    private boolean isDefault;


}
