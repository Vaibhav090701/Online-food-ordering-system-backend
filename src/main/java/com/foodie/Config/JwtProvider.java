 package com.foodie.Config;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;

@Component
@Service
public class JwtProvider {
	
	private SecretKey key=Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
	
	public String generateToken(UserDetails userDetails)
	{
		Collection<?extends GrantedAuthority>authorities=userDetails.getAuthorities();
		String roles=populateAuthorities(authorities);
		
		Instant now=Instant.now();
		
		String jwt=Jwts.builder().setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plusSeconds(86400)))
				.claim("email", userDetails.getUsername())
				.claim("authorities", roles)
				.signWith(key)
				.compact();
		
		return jwt;	
	}
	
	public String getEmailFromJwtToken(String jwt)
	{
		jwt=jwt.substring(7);
		Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
		
		String email=String.valueOf(claims.get("email"));
		
		return email;	
	}
	
	public String populateAuthorities(Collection<? extends GrantedAuthority>authorities)
	{
		Set<String>auths=new HashSet<>();
		
		for(GrantedAuthority authority:authorities)
		{
			auths.add(authority.getAuthority());		
		}
		
		return String.join(",", auths);
	}

}
