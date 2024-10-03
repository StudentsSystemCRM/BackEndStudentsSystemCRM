package edutrack.security.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    JwtTokenProvider jwtTokenProvider;
    AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // Checking token availability
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.replace("Bearer ", "");

            try {
                // Token validation attempt
                Claims claims = jwtTokenProvider.validateAccessToken(jwt);
                email = claims.getSubject();
            } catch (ExpiredJwtException e) {
            	response.setStatus(HttpStatus.UNAUTHORIZED.value());
            	response.getWriter().write("Access token expired");
            	response.getWriter().flush();
            	return;
            } catch (Exception e) {
            	response.setStatus(HttpStatus.UNAUTHORIZED.value());
            	response.getWriter().write("wrong token");
            	response.getWriter().flush();
            	return;
            }

            // If the email is received, continue processing the request
            if (email != null) {
                UserEntity user = accountRepository.findByEmail(email);
                if (user == null) {
                	response.setStatus(HttpStatus.UNAUTHORIZED.value());
                	response.getWriter().write("User not found in the database.");
                	return;
                }
                
                if (!jwt.equals(user.getAccessToken())){
                	response.setStatus(HttpStatus.UNAUTHORIZED.value());
                	response.getWriter().write("Invalid access token, user isn't 'singin' or token was changed");
                	return;
                }

                // Set up authentication
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email, null, AuthorityUtils.createAuthorityList(user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } else {
        	response.setStatus(HttpStatus.UNAUTHORIZED.value());
        	response.getWriter().write("Authorization header is missing or invalid");
        	response.getWriter().flush();
        	return;
        }

        // Skip filter execution for all requests that require authentication
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equalsIgnoreCase("/api/auth/signin") ||
                path.equalsIgnoreCase("/api/auth/signup") ||
                path.equalsIgnoreCase("/api/auth/refreshtoken") ||
                path.equalsIgnoreCase("/api/auth/signout")||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.equalsIgnoreCase("/swagger-ui.html");
    }
}