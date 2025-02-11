package edutrack.user.service;

import java.security.Principal;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.Role;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessRoleException;
import edutrack.user.repository.AccountRepository;
import edutrack.user.util.EntityDtoUserMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {
	AccountRepository userRepository;
	PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDataResponse updateUser(UserUpdateRequest data) {
		UserEntity user = userRepository.findByEmail(data.getEmail());
		if (user == null) {
			throw new NoSuchElementException("Account with email '%s' not found".formatted(data.getEmail()));
		}
		if (user.getName().equals("ada@gmail.com")) {
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
		}

		checkAccessCurrentAccountChangeUser(user);

		user.setName(data.getName());
		user.setSurname(data.getSurname());
		user.setBirthdate(data.getBirthdate());
		user.setPhone(data.getPhone());
		userRepository.save(user);

		return EntityDtoUserMapper.INSTANCE.userToUserDataResponse(user);
	}

	@Override
	@Transactional
	public void updatePassword(Principal user, PasswordUpdateRequest data) {
		if (user.getName().equals("ada@gmail.com")) {
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
		}
		UserEntity account = userRepository.findByEmail(user.getName());
		account.setHashedPassword(passwordEncoder.encode(data.getPassword()));
		userRepository.save(account);
	}

	@Override
	@Transactional
	public UserDataResponse removeUser(String login) {
		if (login.equals("ada@gmail.com")) {
			throw new NoSuchElementException("you can't change default user Ada Lovelace, create new one");
		}

		UserEntity user = userRepository.findByEmail(login);
		if (user == null) {
			throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
		}

		checkAccessCurrentAccountChangeUser(user);

		userRepository.delete(user);
		return EntityDtoUserMapper.INSTANCE.userToUserDataResponse(user);
	}

	@Override
	@Transactional
	public UserDataResponse addRole(String login, UserRoleRequest data) {
		UserEntity user = userRepository.findByEmail(login);
		if (user == null) {
			throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
		}

		checkAccessChangeRoleUser(user, data.getRole());

		user.getRoles().add(Role.fromValue(data.getRole()));
		userRepository.save(user);

		return EntityDtoUserMapper.INSTANCE.userToUserDataResponse(user);
	}

	@Override
	@Transactional
	public UserDataResponse removeRole(String login, UserRoleRequest data) {
		UserEntity user = userRepository.findByEmail(login);
		if (user == null) {
			throw new NoSuchElementException("Account with login '%s' not found".formatted(login));
		}

		checkAccessChangeRoleUser(user, data.getRole());

		user.getRoles().remove(Role.fromValue(data.getRole()));
		userRepository.save(user);
		return EntityDtoUserMapper.INSTANCE.userToUserDataResponse(user);
	}

	private void checkAccessCurrentAccountChangeUser(UserEntity user) {
		UserAuthInfo authInfo = getCurrentUserAuthInfo();
		String username = authInfo.getUsername();
		boolean isAdmin = authInfo.isAdmin;
		boolean isCeo = authInfo.isCeo;

		// Check if the user is attempting to update someone else's profile
		if (!username.equals(user.getEmail())) {
			// Prevent changing data of a CEO
			if (user.getRoles().contains(Role.CEO) && !isCeo) {
				throw new AccessRoleException("You don't have rules to update this user's profile.");
			}

			// Prevent update if the authenticated user is neither an ADMIN nor a CEO
			if (!isAdmin && !isCeo) {
				throw new AccessRoleException("You don't have rules to  update this user's profile.");
			}

			// Prevent an ADMIN from updating another ADMIN's profile unless the
			// authenticated user is a CEO
			if (user.getRoles().contains(Role.ADMIN) && !isCeo) {
				throw new AccessRoleException("You don't have rules to  update this user's profile.");
			}
		}
	}

	private void checkAccessChangeRoleUser(UserEntity user, String role) {
		UserAuthInfo authInfo = getCurrentUserAuthInfo();
		String username = authInfo.getUsername();
		boolean isAdmin = authInfo.isAdmin;
		boolean isCeo = authInfo.isCeo;
		boolean isUser = authInfo.isUser;

		if (isUser && !isAdmin && !isCeo) {
			throw new AccessRoleException("You can't change role");
		}

		if (username.equals(user.getEmail())) {
			throw new AccessRoleException("You can't change role for yourself");
		}
		if (!isAdmin && !isCeo) {
			throw new AccessRoleException("You don't have rules to update this user's profile.");
		}
		if (user.getRoles().contains(Role.CEO) && !isCeo) {
			throw new AccessRoleException("You don't have rules to update this user's profile.");
		}
		if (user.getRoles().contains(Role.ADMIN) && !isCeo) {
			throw new AccessRoleException("You don't have rules to update this user's profile.");
		}
		if (role.equalsIgnoreCase("ceo") && !isCeo) {
			throw new AccessRoleException("add or remove role 'CEO' can only user with CEO role");
		}
	}

	private UserAuthInfo getCurrentUserAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		boolean isAdmin = authorities.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		boolean isCeo = authorities.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CEO"));

		boolean isUser = authorities.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

		return new UserAuthInfo(username, isAdmin, isCeo, isUser);
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	private static class UserAuthInfo {
		private String username;
		private boolean isAdmin;
		private boolean isCeo;
		private boolean isUser;
	}
}
