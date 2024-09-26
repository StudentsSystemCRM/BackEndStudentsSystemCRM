package edutrack.security.jwt;

import edutrack.security.redis.TokenBlackListService;
import edutrack.user.exception.AccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    JwtTokenProvider jwtTokenProvider;
    TokenBlackListService tokenBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.replace("Bearer ", "");

            try {
                // validation token
                Claims claims = jwtTokenProvider.validateAccessToken(jwt);
                email = claims.getSubject();

                // check token by blacklist
                if (tokenBlackListService.isTokenBlacklisted(jwt)) {
                    throw new AccessException("Token is blacklisted");
                }

                // set auth
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email, null, AuthorityUtils.createAuthorityList(claims.get("roles").toString()));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (ExpiredJwtException e) {
                // if token expired - add it to the blacklist
                tokenBlackListService.addTokenToBlacklist(jwt, "ACCESS_TOKEN");
                throw new AccessException("Access token expired");
            } catch (Exception e) {
                throw new AccessException("Invalid access token");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equalsIgnoreCase("/api/auth/signin") ||
                path.equalsIgnoreCase("/api/auth/signup") ||
                path.equalsIgnoreCase("/api/auth/refreshtoken");
    }
}

// OLD REVISION
//@Override
//protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    final String authorizationHeader = request.getHeader("Authorization");
//
//    String email = null;
//    String jwt = null;
//
//    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//        jwt = authorizationHeader.replace("Bearer ", "");
//
//        try {
//            email = jwtTokenProvider.validateAccessToken(jwt).getSubject();
//        } catch (ExpiredJwtException e) {
//            tokenBlackListService.addTokenToBlacklist(jwt, "ACCESS_TOKEN");
//        }
//    }
//
//    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        Claims claims = jwtTokenProvider.validateAccessToken(jwt);
//        if (!tokenBlackListService.isTokenBlacklisted(jwt)) {
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                    email, null, AuthorityUtils.createAuthorityList(claims.get("roles").toString()));
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        }
//    }
//
//    filterChain.doFilter(request, response);
//}