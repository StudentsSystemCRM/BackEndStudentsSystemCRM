package edutrack.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final Key key;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    public Claims parseClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        logger.info("JWT token validated successfully: {}", token);
        return claims;
    }
}
