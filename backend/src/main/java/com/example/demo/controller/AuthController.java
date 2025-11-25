package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.SignupResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
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
    @Operation(summary = "Register a new user", description = "Create a new user account with name, email, and password. User must login to receive tokens.")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest httpServletRequest) {
        try {
            SignupResponse response = authService.signup(signupRequest, httpServletRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password, returns both access and refresh tokens")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, 
                                  HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) {
        try {
            AuthResponse response = authService.login(loginRequest, httpServletRequest);
            String refreshToken = response.getRefreshToken();
            
            // Set refresh token in HTTP-only cookie for browser security
            if (refreshToken != null) {
                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60)
                        .sameSite("Strict")
                        .build();
                httpServletResponse.addHeader("Set-Cookie", cookie.toString());
            }
            
            // Return response with BOTH tokens visible (as requested by teacher)
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchange a valid refresh token for a new access token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
                                         @RequestBody(required = false) java.util.Map<String, String> body) {
        try {
            // Try to get refresh token from cookie first, then from request body
            String refreshToken = cookieRefreshToken;
            if (refreshToken == null && body != null) {
                refreshToken = body.get("refreshToken");
            }
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is required");
            }
            
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
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