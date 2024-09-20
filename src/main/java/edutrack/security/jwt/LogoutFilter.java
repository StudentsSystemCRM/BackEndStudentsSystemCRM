package edutrack.security.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class LogoutFilter extends OncePerRequestFilter {

	JwtUtils jwtUtils;
	RefreshTokenService refreshTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
				String userEmail = authentication.getName(); 
				refreshTokenService.deleteByUserEmail(userEmail);
			}

			ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
			ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

			response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write("{\"message\": \"You've been signed out!\"}");
			response.setStatus(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write("{\"message\": \"Internal Server Error\"}");
		}

	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		return !path.equalsIgnoreCase("/api/auth/signout");
	}
}
