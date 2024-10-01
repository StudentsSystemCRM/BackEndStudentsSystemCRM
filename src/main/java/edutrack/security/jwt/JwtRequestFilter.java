package edutrack.security.jwt;

import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // to handle the case if the access token has expired and we cannot execute 'signOut'
        boolean isSignOutRequest = request.getRequestURI().equalsIgnoreCase("/api/auth/signout");

        // Checking token availability
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.replace("Bearer ", "");

            try {
                // Token validation attempt
                Claims claims = jwtTokenProvider.validateAccessToken(jwt);
                email = claims.getSubject();
            } catch (ExpiredJwtException e) {
                // If the token has expired but the request is for 'signout', continue processing
                // I know it's not the best solution, but I don't have any more ideas - Zhirov Dmitrii
                if (isSignOutRequest) {
                    //Retrieving an email from an expired token
                    email = e.getClaims().getSubject();
                } else {
                    throw new AccessException("Access token expired");
                }
            }

            // If the email is received, continue processing the request
            if (email != null) {
                UserEntity user = accountRepository.findByEmail(email);
                if (user == null) {
                    throw new AccessException("User not found in the database.");
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
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }

        // Skip filter execution for all requests that require authentication
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