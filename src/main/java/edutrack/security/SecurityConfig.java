package edutrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edutrack.user.entity.UserEntity;
import edutrack.user.repository.AccountRepository;

@Configuration
public class SecurityConfig {
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(AccountRepository repository) {
		return (userData) -> {
			UserEntity account = repository.findByEmail(userData);
			if (account == null) {
				throw new UsernameNotFoundException("Account with email '%s' not found".formatted(userData));
			}
			String password = account.getHashedPassword();
			String[] roles = account.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
			return new User(userData, password, AuthorityUtils.createAuthorityList(roles));
		};
	}
}
