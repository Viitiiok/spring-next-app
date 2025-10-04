package com.example.demo.controller;

import com.example.demo.security.AuthFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthFactoryController {

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String secondPassword,
            @RequestParam String role) {

        AuthFactory.Authenticator authenticator = AuthFactory.getAuthenticator(role);
        boolean success;

        if ("ADMIN".equalsIgnoreCase(role)) {
            success = authenticator.authenticate(username, password, secondPassword);
        } else {
            success = authenticator.authenticate(username, password);
        }

        if (success) {
            return ResponseEntity.ok("Authentication successful for " + role);
        } else {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}
