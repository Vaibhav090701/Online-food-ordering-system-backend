package com.foodie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
	
	private final JavaMailSender sender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromMail;
	
	@Override
	public void sendWelcomeMail(String email, String username) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom(fromMail);
		message.setTo(email);
		message.setSubject("Welcome to foddiee");
		message.setText("Hello "+username+ "\n\n Thanks for registering with foodiee!!!\n\n Explore the restaurant and order your favourite food...");
		sender.send(message);
	}

	@Override
	public void sendOtpEmail(String email, String otp) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom(fromMail);
		message.setTo(email);
		message.setSubject("Verification OTP");
		message.setText("Your OTP is: "+otp);
		sender.send(message);
		
	}

	@Override
	public void sendResetOtpEmail(String email, String otp) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom(fromMail);
		message.setTo(email);
		message.setSubject("Reset OTP");
		message.setText("Reset OTP is: "+otp);
		sender.send(message);
		
	}

}
