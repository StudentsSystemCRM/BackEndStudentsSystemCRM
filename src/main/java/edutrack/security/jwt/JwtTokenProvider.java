package edutrack.security.jwt;

import edutrack.user.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenProvider {
    @Value("${jwt.access.expirationMs}")
    private long accessExpirationMs;

    @Value("${jwt.refresh.expirationMs}")
    private long refreshExpirationMs;

    private String accessSecretKey = System.getenv("JWT_ACCESS_SECRET");

    private String refreshSecretKey  = System.getenv("JWT_REFRESH_SECRET");

    public String generateAccessToken(UserEntity user) {
        return generateToken(user, accessSecretKey, accessExpirationMs, true);
    }

    public String generateRefreshToken(UserEntity user) {
        return generateToken(user, refreshSecretKey, refreshExpirationMs, false);
    }

    public Claims validateAccessToken(String token) {
        return validateToken(token, accessSecretKey);
    }

    public Claims validateRefreshToken(String token) {
        return validateToken(token, refreshSecretKey);

    }

    private String generateToken(UserEntity user, String secretKey, long expirationMs, boolean includeRoles) {
        Map<String, Object> claims = new HashMap<>();
        if (includeRoles) {
            claims.put("roles", user.getRoles());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims validateToken(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secretKey) {
        String hashedSecretKey = generate256BitSecret(secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(hashedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @SneakyThrows
    private String generate256BitSecret(String secretKey) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(secretKey.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

}
