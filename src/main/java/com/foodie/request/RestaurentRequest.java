package com.foodie.request;

import java.util.List;

import org.hibernate.annotations.processing.Pattern;

import lombok.Data;

@Data
public class RestaurentRequest {
	
    private String name;
    
    private String address;
    
    private String phone;
    private String email;
    private String twitter;
    private String instagram;

    
    private boolean status;
    private String description;
    
    	
    private List<String>images;


}
