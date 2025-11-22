package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.JwtUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse signup(SignupRequest signupRequest, HttpServletRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Get or create USER role
        Role userRole = roleRepository.findByRoleEnum(RoleEnum.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleEnum(RoleEnum.USER);
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

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole().getName(),
            savedUser.getRegisteredIp(),
            savedUser.getLastIp()
        );
        authResponse.setLastIpAt(savedUser.getLastIpAt());
        return authResponse;
    }

    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // Get user details
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last IP address and timestamp on each login
        String clientIp = extractClientIp(request);
        user.setLastIp(clientIp);
        user.setLastIpAt(java.time.OffsetDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse(
            token,
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

    /**
     * Extract client IP handling common proxy headers.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For can contain multiple IPs, the first is the client
        return xfHeader.split(",")[0].trim();
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

    // Alternative login method using username
    public AuthResponse loginWithUsername(LoginRequest loginRequest) {
        String requestUsername = loginRequest.getEmail(); // Use email as username since LoginRequest doesn't have getUsername()
        String requestPassword = loginRequest.getPassword();

        User user = userRepository.findByUsername(requestUsername).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User with username " + requestUsername + " not found");
        }
        
        // Authenticate user
        authenticate(user, requestPassword);
        
        // Generate JWT token using existing method
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        
        return buildAuthUserDetails(user, accessToken);
    }

    private void authenticate(User user, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        password
                )
        );
    }

    private AuthResponse buildAuthUserDetails(User user, String accessToken) {
        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null
        );
    }
}