package edutrack.security.jwt;

import java.io.IOException;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import edutrack.security.entity.RefreshTokenEntity;
import edutrack.security.services.RefreshTokenService;
import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.service.AuthService;
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
public class TokenGenerationFilter extends OncePerRequestFilter {

    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    RefreshTokenService refreshTokenService;
    AuthService authService;
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            logger.info("User attempting to log in with email: {}"+ loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("User '{}' logged in successfully"+ userDetails.getUsername());

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

            logger.info("JWT and Refresh tokens added to response for user '{}'"+ userDetails.getUsername());
            LoginSuccessResponse loginResponse = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            loginResponse.setToken(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
            response.setStatus(HttpStatus.OK.value());
        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Invalid email or password\"}");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"message\": \"Internal Server Error\"}");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.equalsIgnoreCase("/api/auth/signin");
    }
}
