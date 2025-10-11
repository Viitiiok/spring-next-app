package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    public static final String [] PUBLIC_ENDPOINTS = {
            "/api/public/**"
    };

    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final UserDetailsServiceImpl userDetailsService;
    private CorsFilter corsFilter;

    @Bean
    public AuthentificationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .sessionManagement(SessionManagmentConfigurer<HttpSecurity> ::sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .aunthenticationProvider(authenticationProvider())
                        .addFilterBefore(JwtAuthFilter, JwtAuthFilter.class)
                        .addFilterBefore(filterChainExceptionHandler, UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}