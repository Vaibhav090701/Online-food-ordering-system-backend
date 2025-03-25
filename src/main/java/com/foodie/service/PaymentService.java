package com.foodie.service;

import com.foodie.dto.OrderDTO;
import com.foodie.dto.PaymentResponse;
import com.foodie.model.Order;
import com.foodie.request.OrderRequest;
import com.stripe.exception.StripeException;

public interface PaymentService {
	
	public PaymentResponse createPaymentLink(Order order) throws StripeException;

}
