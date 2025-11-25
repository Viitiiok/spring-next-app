package com.example.demo.controller;

import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management (ADMIN only)")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole() != null ? user.getRole().getName() : "USER",
                    user.getEnabled(),
                    user.getRegisteredIp(),
                    user.getLastIp(),
                    user.getLastIpAt()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userResponses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Delete a user by ID (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
