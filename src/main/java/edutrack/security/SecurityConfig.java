package edutrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import edutrack.repository.UserRepository;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository repository) {
        return (userData) -> {
            edutrack.entity.accounting.User account = repository.findByEmail(userData);
            if (account == null)
                throw new UsernameNotFoundException("Account with email '%s' not found".formatted(userData));
            String password = account.getHashedPassword();
            String[] roles = account.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
            return new User(userData, password, AuthorityUtils.createAuthorityList(roles));
        };
    }
}
