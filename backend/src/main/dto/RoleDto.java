package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleDto {
    
    private Long id;
    
    @NotBlank(message = "Role name is required")
    @Size(max = 20, message = "Role name must not exceed 20 characters")
    private String name;
    
    // Constructors
    public RoleDto() {
    }
    
    public RoleDto(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
