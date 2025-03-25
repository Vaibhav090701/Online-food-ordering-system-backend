package com.foodie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foodie.dto.OrderDTO;
import com.foodie.dto.PaymentResponse;
import com.foodie.model.Order;
import com.foodie.request.OrderRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentServiceImp implements PaymentService {
	
	@Value("${stripe.api.key}")
	private String stripeSecratKey;


	@Override
	public PaymentResponse createPaymentLink(Order order) throws StripeException {
		Stripe.apiKey=stripeSecratKey;
		
		SessionCreateParams params=SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl("http://localhost:3000/payment/success/"+order.getId())
				.setCancelUrl("http://localhost:3000/payment/fail")
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setQuantity(1L)
						.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
								.setCurrency("usd")
								.setUnitAmount((long)order.getTotalAmount()*100)
								.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
										.setName("foodie")
										.build()
										).build()
								).build()
						).build();
		
		Session session=Session.create(params);
		
		PaymentResponse paymentResponse=new PaymentResponse();
		paymentResponse.setPayment_url(session.getUrl());
		return paymentResponse;

	}

}
