package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user/info")
    public String userEndpoint(Authentication auth) {
        return "Hello " + auth.getName() + "! You are logged in as a user.";
    }
}
