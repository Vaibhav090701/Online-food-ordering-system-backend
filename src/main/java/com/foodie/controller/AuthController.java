package com.foodie.controller;

import com.foodie.Config.JwtProvider;
import com.foodie.Config.JwtUtil;
import com.foodie.dto.ApiResponse;
import com.foodie.dto.AuthResponse;
import com.foodie.dto.UserProfileDTO;
import com.foodie.request.LoginRequest;
import com.foodie.service.TokenBlacklistService;
import com.foodie.service.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserServices userService;
    private final TokenBlacklistService blacklistService;
    private final JwtProvider jwtProvider;
    
    private final JwtUtil jwtUtil;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(@Valid @RequestBody LoginRequest req) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // Load user details and generate JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
            String jwtToken = jwtUtil.generateToken(userDetails);

            // Create HTTP-only cookie for JWT
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();

            // Create AuthResponse
            UserProfileDTO userProfile = userService.getUser(req.getEmail());
            AuthResponse authResponse = new AuthResponse(
                    req.getEmail(),
                    jwtToken,
                    userProfile.getRole().toString(),
                    userProfile.getName()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new ApiResponse<>(authResponse, "Login successful", HttpStatus.OK.value()));

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is incorrect");
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
        }
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<ApiResponse<Boolean>> isAuthenticated(
            @CookieValue(name = "jwt", required = false) String jwt) {
        boolean isAuthenticated = jwt != null && !blacklistService.isTokenBlacklisted(jwt);
        return ResponseEntity.ok(new ApiResponse<>(isAuthenticated, "Authentication status checked", HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // Clear JWT cookie by setting maxAge to 0
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(null, "Logged out successfully", HttpStatus.OK.value()));
    }
}