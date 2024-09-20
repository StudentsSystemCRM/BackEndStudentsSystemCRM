package edutrack.security.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import edutrack.security.services.RefreshTokenService;
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
public class RefreshTokenFilter extends OncePerRequestFilter {

    JwtUtils jwtUtils;
    RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

            if ((refreshToken != null) && (refreshToken.length() > 0)) {
                refreshTokenService.findByToken(refreshToken)
                        .map(refreshTokenService::verifyExpiration)
                        .map(tokenEntity -> {
                            String userEmail = tokenEntity.getUserEmail();
                            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userEmail);
                            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            try {
								response.getWriter().write("{\"message\": \"Token is refreshed successfully!\"}");
							} catch (IOException e) {
								e.printStackTrace();
							}
                            response.setStatus(HttpStatus.OK.value());
                            return tokenEntity;
                        })
                        .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("{\"message\": \"Refresh Token is empty!\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"message\": \"Internal Server Error\"}");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.equalsIgnoreCase("/api/auth/refreshtoken");
    }
}

