package com.foodie.Config;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.foodie.util.CustomeAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
	private final JwtRequestFilter jwtRequestFilter;
	private final CustomeAuthenticationEntryPoint customeAuthenticationEntryPoint;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http.sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(Authorize -> Authorize
				.requestMatchers("/api/admin/**").hasAnyRole("ADMIN")
				.requestMatchers("/signin","/signup", "/send-reset-otp","/reset-password", "/logout")
				.permitAll().anyRequest().authenticated()
				)
		.logout(AbstractHttpConfigurer::disable)
		.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
		.exceptionHandling(ex->ex.authenticationEntryPoint (customeAuthenticationEntryPoint))
		.csrf(csrf->csrf.disable())
		.cors(cors->cors.configurationSource(corsConfigurationSource()));
		return http.build();
	}
	
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
	
	public CorsConfigurationSource corsConfigurationSource()
	{
		return new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cfg=new CorsConfiguration();
				
				cfg.setAllowedOrigins(Arrays.asList(
						"http://localhost:8080",
						"http://localhost:5173",
						"http://localhost:3000"
						));
				
				cfg.setAllowedMethods(Collections.singletonList("*"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
				cfg.setExposedHeaders(Arrays.asList("Authorization"));
				cfg.setMaxAge(3600L);
				
				
				return cfg;
				
			}
		};
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(daoAuthenticationProvider);
		
	}
		
		@Bean
		PasswordEncoder passwordEncoder()
		{
			return new BCryptPasswordEncoder();
		}

}