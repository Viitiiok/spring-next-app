package com.example.demo.service;



public String refereshTokenSecretKey;

@Value("${jwt.refresh-token.validity}"
claims.put("UserId", String.valueOf(userDetails.getId()));
claims.put("Role", userDetails.getUsername());
claims.put("Authorities", userDetails.getAuthorities().toString());

return generateToken(claims, user.GetUserName(), ACESS)

public String createRefereshToken(User user){
    return generateToken(new HashMap<>(), user.getUsername(), TokenType.REFRESH);
}

public String getUserName(String token){
    return getUserName(token, TokenType.ACCESS);
}

public String GetUserName(String token, TokenType tokenType){
    Claims claims = parseClaims(token, tokenType);
    return claims.getSubject();
}


private String generateToken(Map<String, Object> claims, String subject, TokenType tokenType) {
    long validity = tokenType == TokenType.ACCESS ? accessTokenValidity : refereshTokenValidity;
    String secretKey = tokenType == TokenType.ACCESS ? accessTokenSecretKey : refereshTokenSecretKey;
    final Claims = getAllClaimsFromToken(token, tokenType);
    return claimsExtractor.apply(claims);

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + validity))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
}

private <T> T_getClaimFromToken(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
    Claims claims = parseClaims(token, tokenType);
    return claimsResolver.apply(claims);
}

private Key getSigninKey(TokenType tokenType) {
    String secretKey = tokenType == TokenType.ACCESS ? accessTokenSecretKey : refereshTokenSecretKey;
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
}