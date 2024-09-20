package edutrack.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import edutrack.exception.AuthenticationFaildException;
import edutrack.exception.response.GeneralErrorResponse;
import edutrack.security.util.Utils;
import edutrack.user.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = jwtUtils.parseJwt(request);
			logger.info("Received JWT token: {}", jwt);
			if (jwt != null) {
				UserEntity account = jwtUtils.validateJwtToken(jwt);
				String[] roles = account.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account.getEmail(),
						account.getHashedPassword(), AuthorityUtils.createAuthorityList(roles));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// install authentication
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.info("Roles for user '{}': {}", account.getEmail(), AuthorityUtils.createAuthorityList(roles));
			}	
			

		} catch (ExpiredJwtException | IllegalArgumentException e) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(e.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		} catch (AuthenticationFaildException e) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(e.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		} catch (Exception e) {
			GeneralErrorResponse responseData = new GeneralErrorResponse(null, "Internel Server Error");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.getWriter().write(Utils.getJsonFromObjectOrEmtyString(responseData));
			response.getWriter().flush();
			return;
		}
		filterChain.doFilter(request, response);
	}
	
	   @Override
	    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	        String path = request.getRequestURI();
	        return path.equalsIgnoreCase("/api/auth/signin") || path.equalsIgnoreCase("/api/auth/signup")
	        		|| path.equalsIgnoreCase("/api/auth/signout") || path.equalsIgnoreCase("/api/auth/refreshtoken");
	        
	    }
}
