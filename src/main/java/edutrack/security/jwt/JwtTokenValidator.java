//package edutrack.security.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//
//@Service
//@RequiredArgsConstructor
//public class JwtTokenValidator {
//    private final Key key;
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);
//
//    public Claims parseClaims(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims;
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Claims claims = parseClaims(token);
//            Date expirationDate = claims.getExpiration();
//
//            if (expirationDate.before(new Date())) {
//                logger.warn("JWT token is expired: {}", token);
//                return false;
//            }
//
//            logger.info("JWT token is valid: {}", token);
//            return true;
//        } catch (Exception e) {
//            logger.warn("Invalid JWT token: {}", token);
//        }
//        return false;
//    }
//}
