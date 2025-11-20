package com.example.demo.service;

import com.example.demo.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.access-token.secret-key}")
    private String accessTokenSecretKey;

    @Value("${jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKey;

    @Value("${jwt.access-token.validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token.validity}")
    private long refreshTokenValidity;

    public enum TokenType {
        ACCESS, REFRESH
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add custom claims
        if (userDetails instanceof com.example.demo.model.User) {
            com.example.demo.model.User user = (com.example.demo.model.User) userDetails;
            claims.put("userId", String.valueOf(user.getId()));
            claims.put("role", user.getRole().getName());
        }
        claims.put("authorities", userDetails.getAuthorities().toString());
        
        return generateToken(claims, userDetails.getUsername(), TokenType.ACCESS);
    }

    public String createRefreshToken(com.example.demo.model.User user) {
        return generateToken(new HashMap<>(), user.getUsername(), TokenType.REFRESH);
    }

    public String getUsername(String token) {
        return getUsername(token, TokenType.ACCESS);
    }

    public String getUsername(String token, TokenType tokenType) {
        try {
            return getClaimFromToken(token, Claims::getSubject, tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            if (e.getMessage().contains("JWT signature does not match")) {
                throw new JwtAuthenticationException("Invalid JWT signature");
            }
            throw e;
        }
    }

    public boolean isTokenValid(String token, TokenType tokenType) {
        return !isTokenExpired(token, tokenType);
    }

    public boolean isTokenValid(String token, TokenType tokenType, String username) {
        final String extractedUsername = getUsername(token, tokenType);
        return (extractedUsername.equals(username) && !isTokenExpired(token, tokenType));
    }

    private boolean isTokenExpired(String token, TokenType tokenType) {
        Date tokenExpirationDate = getClaimFromToken(token, Claims::getExpiration, tokenType);
        return tokenExpirationDate.before(new Date());
    }

    private String generateToken(Map<String, Object> claims, String subject, TokenType tokenType) {
        long validity = tokenType == TokenType.ACCESS ? accessTokenValidity : refreshTokenValidity;
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(tokenType))
                .compact();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        Claims claims = getAllClaimsFromToken(token, tokenType);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token, TokenType tokenType) {
        return Jwts.parser()
                .verifyWith(getSigningKey(tokenType))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey(TokenType tokenType) {
        String secretKey = tokenType == TokenType.ACCESS ? accessTokenSecretKey : refreshTokenSecretKey;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}