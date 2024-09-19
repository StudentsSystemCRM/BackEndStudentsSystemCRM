package edutrack.security;

import edutrack.security.jwt.AuthEntryPointJwt;
import edutrack.security.jwt.AuthTokenFilter;
import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
//    @Autowired
//    private UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
     AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
//
//    @Bean
//     DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    @Bean
     AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
     PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                                                          // off CSRF for work with token
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))        // error handling
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // session stateless for JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()      // access to swagger UI
                        .requestMatchers("/api/auth/**").permitAll()                                                   // access to auth
                        .requestMatchers("/api/users/assign-role/*", "/api/users/remove-role/*").hasAnyRole("ADMIN", "CEO")  // ONLY ADMIN and CEO can assign/delete roles
                        .requestMatchers(HttpMethod.POST, "/api/students").hasAnyRole("ADMIN", "CEO")           // ONLY ADMIN and CEO can assign students
                        .requestMatchers(HttpMethod.GET, "/api/students/*/payments").hasAnyRole("ADMIN", "CEO") // ONLY ADMIN and CEO can see student payments
                        .requestMatchers(HttpMethod.POST, "/api/students/payment").hasAnyRole("ADMIN", "CEO")   // ONLY ADMIN and CEO can assign payments
                        .requestMatchers(HttpMethod.DELETE, "/api/students/*").hasAnyRole("ADMIN", "CEO")       // ONLY ADMIN and CEO can delete students
                        .anyRequest().authenticated())
//                .authenticationProvider(authenticationProvider())                                               // install auth provider
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);   // add JWT filter

        return http.build();
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
