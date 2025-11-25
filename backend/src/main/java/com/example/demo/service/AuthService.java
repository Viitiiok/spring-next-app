package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.SignupResponse;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Value("${security.trusted-proxies:}")
    private String trustedProxiesConfig;

    private java.util.List<String> trustedProxies = new java.util.ArrayList<>();

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest, HttpServletRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Determine which role to assign (defaults to USER if not specified)
        String requestedRole = signupRequest.getRole();
        RoleEnum roleEnum = RoleEnum.USER; // default
        
        if (requestedRole != null && !requestedRole.isEmpty()) {
            try {
                roleEnum = RoleEnum.valueOf(requestedRole.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid role provided, default to USER
                roleEnum = RoleEnum.USER;
            }
        }
        
        // Get or create the role
        final RoleEnum finalRoleEnum = roleEnum;
        Role userRole = roleRepository.findByRoleEnum(finalRoleEnum)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleEnum(finalRoleEnum);
                    return roleRepository.save(newRole);
                });

        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getEmail()); // Use email as username
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(userRole);
        user.setEnabled(true);

        // Capture client IP and set registeredIp / lastIp and timestamp
        String clientIp = extractClientIp(request);
        user.setRegisteredIp(clientIp);
        user.setLastIp(clientIp);
        user.setLastIpAt(java.time.OffsetDateTime.now());

        User savedUser = userRepository.save(user);

        // Return user info without any tokens (user must login to get tokens)
        SignupResponse signupResponse = new SignupResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole().getName(),
            savedUser.getRegisteredIp(),
            savedUser.getLastIp()
        );
        signupResponse.setLastIpAt(savedUser.getLastIpAt());
        return signupResponse;
    }

    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Generate JWT tokens (access + refresh)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        // Get user details
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String refreshToken = jwtService.createRefreshToken(user);

        // Update last IP address and timestamp on each login
        String clientIp = extractClientIp(request);
        user.setLastIp(clientIp);
        user.setLastIpAt(java.time.OffsetDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse(
            accessToken,
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName(),
            user.getRegisteredIp(),
            user.getLastIp()
        );
        authResponse.setRefreshToken(refreshToken);
        authResponse.setLastIpAt(user.getLastIpAt());
        return authResponse;
    }

    /**
     * Extract client IP handling common proxy headers.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        String remoteAddr = request.getRemoteAddr();

        if (xfHeader == null) {
            return remoteAddr;
        }

        // If no trusted proxies configured, fall back to old behavior (accept XFF).
        if (trustedProxies.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        // Only accept X-Forwarded-For if the immediate peer (remoteAddr) is in the trusted proxies list.
        for (String trusted : trustedProxies) {
            if (trusted.isEmpty()) continue;
            if (remoteAddr.equals(trusted) || remoteAddr.startsWith(trusted)) {
                return xfHeader.split(",")[0].trim();
            }
        }

        // Not from a trusted proxy; ignore X-Forwarded-For
        return remoteAddr;
    }

    @PostConstruct
    private void initTrustedProxies() {
        if (trustedProxiesConfig == null || trustedProxiesConfig.isBlank()) return;
        String[] parts = trustedProxiesConfig.split(",");
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) trustedProxies.add(t);
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token and extract username
        String username = jwtService.getUsername(refreshToken, JwtService.TokenType.REFRESH);
        
        if (username == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Check if token is valid
        if (!jwtService.isTokenValid(refreshToken, JwtService.TokenType.REFRESH)) {
            throw new RuntimeException("Refresh token has expired");
        }
        
        // Load user details
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails);
        
        // Return response with new access token (optionally generate new refresh token too)
        AuthResponse authResponse = new AuthResponse(
            newAccessToken,
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName(),
            user.getRegisteredIp(),
            user.getLastIp()
        );
        authResponse.setLastIpAt(user.getLastIpAt());
        return authResponse;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication)) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Clear any JWT cookies if they exist
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        // Clear the security context
        SecurityContextHolder.clearContext();
    }
}