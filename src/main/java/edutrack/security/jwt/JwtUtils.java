package edutrack.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import edutrack.exception.AuthenticationFaildException;
import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;

import java.security.Key;
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
    
    @Autowired
    AccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // generate token after success authentication user (for login method - look at AuthController)
    public ResponseCookie generateJwtCookie(UserDetails userPrincipal) {
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
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        ResponseCookie refreshCookie = generateCookie(jwtRefreshCookie, refreshToken, "/api/auth/refreshtoken");
        logger.info("Token generated REFRESH JWT COOKIE: {}", refreshCookie);
        return  refreshCookie;
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
                .setAllowedClockSkewSeconds(60)
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

    public UserEntity validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build().parse(authToken);

            logger.info("JWT token is valid");
            String username = getUserNameFromJwtToken(authToken);
            UserEntity userEntity = accountRepository.findByEmail(username);
            return userEntity;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new AuthenticationFaildException("Invalid token value, " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new ExpiredJwtException(null, null, "JWT token is expired");
           // throw new AuthenticationFaildException("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new AuthenticationFaildException("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw new IllegalArgumentException("JWT claims string is empty: " + e.getMessage());
        }
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
    
	public String parseJwt(HttpServletRequest request) {
		String jwt = getJwtFromCookies(request);
		if (jwt == null) {
			logger.warn("JWT token is not found in cookies");
		} else {
			logger.info("JWT token found in cookies: {}", jwt);
		}
		return jwt;
	}
}
