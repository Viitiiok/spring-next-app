package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthUserDetails {
    private Long id;
    private String name;
    private String email;
    private String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String role;
    private Boolean enabled;
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;

    // Static factory method to create AuthUserDetails from User entity
    public static AuthUserDetails fromUser(com.example.demo.model.User user) {
        return AuthUserDetails.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .enabled(user.getEnabled())
                .build();
    }

    // Static factory method to create AuthUserDetails with tokens
    public static AuthUserDetails withTokens(com.example.demo.model.User user, String accessToken, String refreshToken) {
        return AuthUserDetails.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .enabled(user.getEnabled())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}