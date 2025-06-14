package com.foodie.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.UserRepository;


@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("user not found with email "+username));
				
		Role role=user.getRole();
		
		List<GrantedAuthority> authorities=new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role.toString()));
		
						
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

}
