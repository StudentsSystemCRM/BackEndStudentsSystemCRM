package edutrack.security.jwt;

import edutrack.security.services.UserDetailsImpl;
import edutrack.user.entity.UserEntity;
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

    @Value("${jwt.refreshCookieName}")
    private String jwtRefreshCookie;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // generate token after success authentication user (for login method - look at AuthController)
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());        // create cookie
        logger.info("JWT token generated for user: {}", userPrincipal.getUsername());

        return generateCookie(jwtCookie, jwt, "/api");
    }

    public ResponseCookie generateJwtCookie(String userEmail) {
        String jwt = generateTokenFromUsername(userEmail);
        logger.info("JWT token generated for userEntity: {}", userEmail);

        return generateCookie(jwtCookie, jwt, "/api");
    }

    // generate token bu email (username = email)
    public String generateTokenFromUsername(String username) {
        Instant now = Instant.now(Clock.systemUTC());
        logger.info("Token generated at UTC time: {}", now);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return  generateCookie(jwtRefreshCookie, refreshToken, "/api/auth/refreshtoken");
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    // get email method from JWT (email = username)
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // create cookie without JWT (for logout - look at AuthController)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null)
                .path("/api/auth/refreshtoken")
                .build();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Instant now = Instant.now(Clock.systemUTC());
            logger.info("Current UTC time: {}", now);

            Jwts.parserBuilder()
                    .setSigningKey(key())
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

    // create the key for subscribe JWT
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)       // create cookie with token
                .path(path)                           // access for all paths
                .maxAge(24 * 60 * 60)   // time live = 24 hours
                .httpOnly(true).build();              // HttpOnly for security
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);  // get token from cookie
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
