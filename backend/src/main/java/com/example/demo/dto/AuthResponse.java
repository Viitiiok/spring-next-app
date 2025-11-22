package com.example.demo.dto;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private String role;
    private String refreshToken;
    private String registeredIp;
    private String lastIp;
    private java.time.OffsetDateTime lastIpAt;
    
    // Constructors
    public AuthResponse() {
    }
    
    public AuthResponse(String token, Long id, String name, String email, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public AuthResponse(String token, Long id, String name, String email, String role, String registeredIp, String lastIp) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.registeredIp = registeredIp;
        this.lastIp = lastIp;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRegisteredIp() {
        return registeredIp;
    }

    public void setRegisteredIp(String registeredIp) {
        this.registeredIp = registeredIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public java.time.OffsetDateTime getLastIpAt() {
        return lastIpAt;
    }

    public void setLastIpAt(java.time.OffsetDateTime lastIpAt) {
        this.lastIpAt = lastIpAt;
    }
}
