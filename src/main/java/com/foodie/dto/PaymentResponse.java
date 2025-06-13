package com.foodie.dto;

import lombok.Data;

@Data
public class PaymentResponse {
	
	private String payment_url;
	
	private boolean success;
    private String message;
    private String transactionId;

}
