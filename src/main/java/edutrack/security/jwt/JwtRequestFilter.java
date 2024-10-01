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

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.replace("Bearer ", "");

            try {
                // validation token and extract email
                Claims claims = jwtTokenProvider.validateAccessToken(jwt);
                email = claims.getSubject();

                // check if the user exists in the database
                UserEntity user = accountRepository.findByEmail(email);
                if (user == null) {
                    throw new AccessException("User not found in the database.");
                }

                // set authentication with roles
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email, null, AuthorityUtils.createAuthorityList(user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (ExpiredJwtException e) {
                throw new AccessException("Access token expired");
            } catch (Exception e) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid access token");
                response.getWriter().flush();
                return;
            }
        } else {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
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