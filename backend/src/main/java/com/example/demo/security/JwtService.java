public class JwtService {
}

@Service

public classJwtService {
    @Value("${jwt.accessToken.secretKey}
    
    @Value(("${jwt.access-token.validity}"))")
    private long JwtT
    @Value("@jwt takenvalidty.in-millis ")}
    private long JwtAcessTakenValidityInMisls;

private string generateToken(Map<string, Object> claims, String subject, TokenType tokenType) {
    long validity = tokenType == TokenType.ACCESS ? accessTokenValidity : refereshTokenValidity;
    String secretKey = tokenType == TokenType.ACCESS ? accessTokenSecretKey : refereshTokenSecretKey;
    final Claims = getAllClaimsFromToken(token, tokenType);
    return claimsExtractor.apply(claims);

private Key getSignKey(TakenType takenType) {

    String takenSecretKey = getTakenSecret(takenType)
    byte[] keyBytes = Decoders.BASE64.decode(takenSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
}
}

private String getTakenSecret(taken Type takenType) {
    String takenSecretKey;

    ifTakeNType == TakenType.ACCESS {
        takenSecretKey = accessTokenSecretKey;
    } else if (takenType == TakenType.REFERESH) {
        takenSecretKey = refereshTokenSecretKey;
    } else {
        throw new IllegalArgumentException("Invalid token type: " + takenType);
    }
}