package edutrack.user.service;

import java.security.Principal;
import java.util.*;

import edutrack.security.JwtTokenCreator;
import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.Role;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.ResourceExistsException;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edutrack.user.repository.AccountRepository;
import edutrack.user.util.EntityDtoUserMapper;
import jakarta.transaction.Transactional;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {

	AccountRepository userRepository;
	PasswordEncoder passwordEncoder;
	JwtTokenCreator jwtTokenCreator;

	@Override
	@Transactional
	public LoginSuccessResponse registration(String invite, UserRegisterRequest data) {

		// TODO invite check invite

		UserEntity user = userRepository.findByEmail(data.getEmail());

		if (user != null) {
			throw new ResourceExistsException("user with email " + data.getEmail() + "already exists");
		}
		user = EntityDtoUserMapper.INSTANCE.userRegisterRequestToUser(data);
		user.setHashedPassword(passwordEncoder.encode(data.getPassword()));
		user.setRoles(new HashSet<>(List.of(Role.USER)));
		userRepository.save(user);
		// token
		String token = jwtTokenCreator.createToken(user.getEmail(), user.getRoles());
		LoginSuccessResponse response = EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(user);
		response.setToken(token);

		return response;
	}

	@Override
	public LoginSuccessResponse login(Principal user) {
		UserEntity userService = userRepository.findByEmail(user.getName());
		// token
		String token = jwtTokenCreator.createToken(userService.getEmail(), userService.getRoles());
		LoginSuccessResponse response = EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(userService);
		response.setToken(token);
		return response;
	}

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
			if (user.getRoles().contains(Role.CEO)) {
				throw new AccessException("You don't have rules to update this user's profile.");
			}

			// Prevent update if the authenticated user is neither an ADMIN nor a CEO
			if (!isAdmin && !isCeo) {
				throw new AccessException("You don't have rules to  update this user's profile.");
			}

			// Prevent an ADMIN from updating another ADMIN's profile unless the
			// authenticated user is a CEO
			if (user.getRoles().contains(Role.ADMIN) && !isCeo) {
				throw new AccessException("You don't have rules to  update this user's profile.");
			}
		}
	}

	private void checkAccessChangeRoleUser(UserEntity user, String role) {
		UserAuthInfo authInfo = getCurrentUserAuthInfo();
		String username = authInfo.getUsername();
		boolean isAdmin = authInfo.isAdmin;
		boolean isCeo = authInfo.isCeo;

		if (username.equals(user.getEmail())) {
			throw new AccessException("You can't change role for yourself");
		}
		if (!isAdmin && !isCeo) {
			throw new AccessException("You don't have rules to update this user's profile.");
		}
		if (user.getRoles().contains(Role.CEO) && !isCeo) {
			throw new AccessException("You don't have rules to update this user's profile.");
		}
		if (user.getRoles().contains(Role.ADMIN) && !isCeo) {
			throw new AccessException("You don't have rules to update this user's profile.");
		}
		if (role.equalsIgnoreCase("ceo") && !isCeo) {
			throw new AccessException("add or remove role 'CEO' can only user with CEO role");
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

		return new UserAuthInfo(username, isAdmin, isCeo);
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	private static class UserAuthInfo {
		private String username;
		private boolean isAdmin;
		private boolean isCeo;
	}
}
