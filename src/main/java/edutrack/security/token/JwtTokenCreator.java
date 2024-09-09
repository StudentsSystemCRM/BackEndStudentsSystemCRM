package edutrack.security.token;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edutrack.modul.user.dto.response.Role;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenCreator {
    private final Key key;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenCreator.class);

    @Value("${jwt.expirationTime}")
    private long expirationTime;

    public String createToken(String email, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();

        logger.info("JWT token created for user: {}", email);
        return token;
    }
}
