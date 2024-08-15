package edutrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AutorizationFilterChain {
	
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http.httpBasic(Customizer.withDefaults())
				.csrf(csrf->csrf.disable())
				.sessionManagement(sessionManagment -> sessionManagment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize-> authorize
						.requestMatchers("/api/users/register").permitAll()
						.requestMatchers("/api/users/assign-role/*").hasAnyRole("ADMIN", "CEO")
						.requestMatchers("/api/users/remove-role/*").hasAnyRole("ADMIN", "CEO")
						
						.requestMatchers(HttpMethod.POST, "/api/students").hasAnyRole("ADMIN", "CEO")
						.requestMatchers(HttpMethod.GET, "/api/students/*/payments").hasAnyRole("ADMIN", "CEO")
						.requestMatchers(HttpMethod.POST, "/api/students/payment").hasAnyRole("ADMIN", "CEO")
						.requestMatchers(HttpMethod.DELETE, "/api/students/*").hasAnyRole("ADMIN", "CEO")
						.anyRequest().authenticated()
						)
				.build();
		
	}

}
