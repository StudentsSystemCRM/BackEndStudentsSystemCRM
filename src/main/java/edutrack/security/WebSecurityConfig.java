package edutrack.security;

import edutrack.security.jwt.AuthEntryPointJwt;
import edutrack.security.jwt.AuthTokenFilter;
import edutrack.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // off CSRF for work with token
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // error handling
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session stateless for JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()  // Разрешаем доступ к Swagger UI
                        .requestMatchers("/api/auth/**").permitAll()  // Разрешаем доступ к авторизации
                        .requestMatchers("/api/users/assign-role/*", "/api/users/remove-role/*").hasAnyRole("ADMIN", "CEO")  // Только ADMIN и CEO могут назначать/удалять роли
                        .requestMatchers(HttpMethod.POST, "/api/students").hasAnyRole("ADMIN", "CEO")  // Только ADMIN и CEO могут добавлять студентов
                        .requestMatchers(HttpMethod.GET, "/api/students/*/payments").hasAnyRole("ADMIN", "CEO")  // Только ADMIN и CEO могут просматривать платежи студентов
                        .requestMatchers(HttpMethod.POST, "/api/students/payment").hasAnyRole("ADMIN", "CEO")  // Только ADMIN и CEO могут добавлять платежи студентов
                        .requestMatchers(HttpMethod.DELETE, "/api/students/*").hasAnyRole("ADMIN", "CEO")  // Только ADMIN и CEO могут удалять студентов
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider()) // install auth provider
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // add JWT filter

        return http.build();
    }
}
