package edutrack.security;

import edutrack.elasticsearch.service.ElasticsearchLogging;
import edutrack.security.jwt.JwtRequestFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
	JwtRequestFilter jwtRequestFilter;
	ElasticsearchLogging elasticsearchLogging;

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http.httpBasic(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/signup", "api/auth/refreshtoken", "/api/auth/signout").permitAll()
						.requestMatchers("/api/users/update", "api/users/assign-role/*", "api/users/remove-role/*")
						.hasAnyRole("ADMIN", "CEO", "USER").requestMatchers(HttpMethod.DELETE, "/api/users/*")
						.hasAnyRole("ADMIN", "CEO", "USER")

						.requestMatchers(HttpMethod.POST, "/api/students").hasAnyRole("ADMIN", "CEO")
						.requestMatchers(HttpMethod.DELETE, "/api/students/*").hasAnyRole("ADMIN", "CEO").anyRequest()
						.authenticated())
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint((request, response, authException) -> {
								String username = getUserName(request.getHeader("Authorization"));
								String message = "Authentication failed for user: " + username;
								elasticsearchLogging.saveLog(message, null, request.getRequestURI(),
										request.getMethod(), username);
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
								response.getWriter().write("Authentication failed: " + authException.getMessage());
								response.getWriter().flush();
						}))
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(AccountRepository repository) {
		return (userData) -> {
			UserEntity account = repository.findByEmail(userData);
			if (account == null) {
				String message = "Account with email '%s' not found".formatted(userData);
				elasticsearchLogging.saveLog(message, null, null, null, userData);
				throw new UsernameNotFoundException(message);
			}
			String password = account.getHashedPassword();
			String[] roles = account.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
			return new User(userData, password, AuthorityUtils.createAuthorityList(roles));
		};
	}
	
	private String getUserName(String authHeader) {
		String userName = "Anonymous";
		if (authHeader != null && authHeader.startsWith("Basic ")) {
			String base64Credentials = authHeader.substring("Basic ".length());
			byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
			String decodedCredentials = new String(decodedBytes, StandardCharsets.UTF_8);
			String[] credentials = decodedCredentials.split(":", 2);
			userName = credentials[0];
		}
		return userName;
	}

}
