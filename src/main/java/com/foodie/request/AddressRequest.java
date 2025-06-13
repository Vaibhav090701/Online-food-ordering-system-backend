package com.foodie.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    private String zipCode;

    private String landmark;

    private boolean isDefault;

}
