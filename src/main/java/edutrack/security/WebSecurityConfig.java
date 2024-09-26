package edutrack.security;

import edutrack.security.jwt.JwtRequestFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    JwtRequestFilter jwtRequestFilter;
    AuthenticationUserFilter authenticationUserFilter;

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("CEO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/students").hasAnyRole("ADMIN", "CEO")
                        .requestMatchers(HttpMethod.GET, "/api/students/*/payments").hasAnyRole("ADMIN", "CEO")
                        .requestMatchers(HttpMethod.POST, "/api/students/payment").hasAnyRole("ADMIN", "CEO")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/*").hasAnyRole("ADMIN", "CEO")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authenticationUserFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountRepository repository) {
        return (userData) -> {
            UserEntity account = repository.findByEmail(userData);
            if (account == null)
                throw new UsernameNotFoundException("Account with email '%s' not found".formatted(userData));
            String password = account.getHashedPassword();
            String[] roles = account.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
            return new User(userData, password, AuthorityUtils.createAuthorityList(roles));
        };
    }
}
