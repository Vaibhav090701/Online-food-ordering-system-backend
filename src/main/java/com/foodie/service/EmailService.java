package com.foodie.service;

public interface EmailService {

	void sendWelcomeMail(String email, String username);

	void sendOtpEmail(String email, String otp);

	void sendResetOtpEmail(String email, String otp);

}
