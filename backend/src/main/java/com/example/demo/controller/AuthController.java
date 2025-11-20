package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login and signup")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account with name, email, and password")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            AuthResponse response = authService.signup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password, returns JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, 
                                  HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) {
        try {
            AuthResponse response = authService.login(loginRequest);
            
            // If you want to set refresh token in cookie, you'll need to modify your AuthService
            // to return refresh tokens and then set them here
            /*
            String refreshToken = response.getRefreshToken();
            if (refreshToken != null) {
                String cookieValue = "refreshToken=" + refreshToken + 
                                   "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=" + (7 * 24 * 60 * 60);
                httpServletResponse.addHeader("Set-Cookie", cookieValue);
                response.setRefreshToken(null); // Remove from response body
            }
            */
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidate user session and clear authentication tokens")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.logout(request, response);
            return ResponseEntity.ok().body("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }
    }
}