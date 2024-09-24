package edutrack.security.jwt;

import edutrack.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.access.secretKey}")
    private String accessSecretKey;

    @Value("${jwt.access.expirationMs}")
    private long accessExpirationMs; // 30000ms = 30sec

    @Value("${jwt.refresh.secretKey}")
    private String refreshSecretKey;

    @Value("${jwt.refresh.expirationMs}")
    private long refreshExpirationMs; // 60000ms = 60sec

    // ===== TOKEN GENERATION ======

    public String generateAccessToken(UserEntity user) {
        return generateToken(user, accessSecretKey, accessExpirationMs);
    }

    public String generateRefreshToken(UserEntity user) {
        return generateToken(user, refreshSecretKey, refreshExpirationMs);
    }

    private String generateToken(UserEntity user, String secretKey, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===== TOKEN VALIDATION =====

    public Claims validateAccessToken(String token) {
        return validateToken(token, accessSecretKey);
    }

    public Claims validateRefreshToken(String token) {
        return validateToken(token, refreshSecretKey);
    }

    private Claims validateToken(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===== TOKEN HELPERS ======

    public boolean isTokenValid(String token, UserEntity user) {
        String username = extractClaim(token, Claims::getSubject);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(accessSecretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
