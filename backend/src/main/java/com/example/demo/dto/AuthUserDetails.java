package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthUserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
    private Boolean enabled = true;
    private String access_token;
    private String refresh_token;

    // Getters and Setters
    public static AunthentificatedUserDetails fromAunthentificatedUser(AuthenticatedUser user) {
        return AunthentificatedUserDetails.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().getRoleEnum().name())
                .enabled(user.getEnabled())
                .build();
    }

 AuhentificatedUSerDetails userDetails = authService.login(httpServletRequest, loginRequest);
        // Set JWT token in HttpOnly cookie
        Cookie jwtCookie = new Cookie("jwtToken", userDetails.getJwtToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(userDetails);
}