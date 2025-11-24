package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
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

    public String generateAccessToken(String username) {
        return generateToken(Map.of(), username, TokenType.ACCESS);
    }

    public String generateRefreshToken(String username) {
        return generateToken(Map.of(), username, TokenType.REFRESH);
    }

    public String generateToken(Map<String, Object> claims, String subject, TokenType tokenType) {
        long validity = tokenType == TokenType.ACCESS ? accessTokenValidity : refreshTokenValidity;
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSignKey(tokenType))
                .compact();
    }

    public String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    public Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, TokenType tokenType, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token, tokenType);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token, TokenType tokenType) {
        return Jwts.parser()
                .verifyWith(getSignKey(tokenType))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date());
    }

    public Boolean validateToken(String token, String username, TokenType tokenType) {
        final String extractedUsername = extractUsername(token, tokenType);
        return (extractedUsername.equals(username) && !isTokenExpired(token, tokenType));
    }

    private SecretKey getSignKey(TokenType tokenType) {
        String tokenSecret = getTokenSecret(tokenType);
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getTokenSecret(TokenType tokenType) {
        if (tokenType == TokenType.ACCESS) {
            return accessTokenSecretKey;
        } else if (tokenType == TokenType.REFRESH) {
            return refreshTokenSecretKey;
        } else {
            throw new IllegalArgumentException("Invalid token type: " + tokenType);
        }
    }
}