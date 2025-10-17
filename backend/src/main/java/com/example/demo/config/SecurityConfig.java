package com.example.demo.config;

import com.example.demo.security.CorsFilter;
import com.example.demo.security.FilterChainExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/public/**"
    };

    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final CorsFilter corsFilter;

    @Autowired
    public SecurityConfig(FilterChainExceptionHandler filterChainExceptionHandler,
                          CorsFilter corsFilter) {
        this.filterChainExceptionHandler = filterChainExceptionHandler;
        this.corsFilter = corsFilter;
    }

    // Authentication is configured by exposing a PasswordEncoder and a UserDetailsService bean
    // Spring Security will wire them into the AuthenticationManager automatically.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // AuthenticationManager is provided via AuthenticationConfiguration bean

        // Exception handler and CORS filter placed before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(filterChainExceptionHandler, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
        );

    http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}