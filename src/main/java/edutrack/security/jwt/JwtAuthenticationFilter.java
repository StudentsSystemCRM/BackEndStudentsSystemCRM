package edutrack.security.jwt;

import edutrack.security.jwt.redis.TokenBlackListService;
import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtService;
    AccountRepository accountRepository;
    TokenBlackListService tokenBlackListService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            // check token in redis DB
            if (tokenBlackListService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            try {
                email = jwtService.extractClaim(token, Claims::getSubject);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        }

        if (token != null && jwtService.isTokenExpired(token)) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
            response.getWriter().write("Access token expired");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity user = accountRepository.findByEmail(email);

            if (user != null && jwtService.isTokenValid(token, user)) {
                Claims claims = jwtService.validateAccessToken(token);
                List<String> roles = (List<String>) claims.get("roles");
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
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