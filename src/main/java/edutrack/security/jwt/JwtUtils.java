package edutrack.security.jwt;

import edutrack.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @Value("${jwt.cookieName}")
    private String jwtCookie;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie); // get token from cookie
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    // generate token after success authentication user (for login method - look at AuthController)
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());        // create cookie
        logger.info("JWT token generated for user: {}", userPrincipal.getUsername());

        return ResponseCookie.from(jwtCookie, jwt)                  // create cookie with token
                .path("/api").maxAge(24 * 60 * 60)    // access for all paths, and time live = 24 hours
                .httpOnly(true).build();                            // HttpOnly for security
    }

    // generate token bu email (username = email)
    public String generateTokenFromUsername(String username) {
        Instant now = Instant.now(Clock.systemUTC());
        logger.info("Token generated at UTC time: {}", now);

        return Jwts.builder().setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // get email method from JWT (email = username)
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // create cookie without JWT (for logout - look at AuthController)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api").build();
    }

    // create the key for subscribe JWT
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Instant now = Instant.now(Clock.systemUTC());
            logger.info("Current UTC time: {}", now);

            Jwts.parserBuilder().setSigningKey(key())
                    .setAllowedClockSkewSeconds(60)
                    .build().parse(authToken);

            logger.info("JWT token is valid");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
