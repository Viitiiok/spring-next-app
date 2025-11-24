package com.example.demo.dto;

public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean enabled;
    private String registeredIp;
    private String lastIp;
    private java.time.OffsetDateTime lastIpAt;
    
    // Constructors
    public UserResponse() {
    }
    
    public UserResponse(Long id, String name, String email, String role, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public UserResponse(Long id, String name, String email, String role, Boolean enabled, String registeredIp, String lastIp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.registeredIp = registeredIp;
        this.lastIp = lastIp;
    }

    public UserResponse(Long id, String name, String email, String role, Boolean enabled, String registeredIp, String lastIp, java.time.OffsetDateTime lastIpAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.registeredIp = registeredIp;
        this.lastIp = lastIp;
        this.lastIpAt = lastIpAt;
    }

    public java.time.OffsetDateTime getLastIpAt() {
        return lastIpAt;
    }

    public void setLastIpAt(java.time.OffsetDateTime lastIpAt) {
        this.lastIpAt = lastIpAt;
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
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
}
