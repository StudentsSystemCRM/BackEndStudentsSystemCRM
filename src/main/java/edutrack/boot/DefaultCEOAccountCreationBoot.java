package edutrack.boot;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edutrack.modul.user.repository.AccountRepository;
import edutrack.modul.user.dto.response.Role;
import edutrack.modul.user.entity.Account;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultCEOAccountCreationBoot implements ApplicationRunner{
	PasswordEncoder passwordEncoder;
	AccountRepository repository;
	
	@Override
	public void run(ApplicationArguments args) {
		try {
			Account user = repository.findByEmail("ada@gmail.com");
			if(user == null) {
				Set<Role> roles = new HashSet<>();
				roles.add(Role.CEO);
				String password = passwordEncoder.encode("12345");
				LocalDate birthdate = LocalDate.of(1815, 12, 10);
				user = new Account("0", "ada@gmail.com", password, "Ada", "Lovelace", null, birthdate, LocalDate.now(), roles);
				repository.save(user);
			}
		} catch (DataAccessException e) {
			throw new RuntimeException("Database error occurred",e);
		} catch (Exception e) {
			throw new RuntimeException("An unexpected error occurred",e);
		}
	}
}
