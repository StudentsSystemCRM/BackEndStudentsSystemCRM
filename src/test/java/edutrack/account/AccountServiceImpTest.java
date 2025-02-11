package edutrack.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.Role;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessRoleException;
import edutrack.user.repository.AccountRepository;
import edutrack.user.service.AccountServiceImp;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountServiceImpTest {
	@InjectMocks
	AccountServiceImp accountingManagementService;

	@Mock
	AccountRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	Authentication authentication;

	@Mock
	SecurityContext securityContext;

	final String userEmail = "test@mail.com";
	final String adminEmail = "admin@mail.com";
	final UserUpdateRequest userUpdateRequest = new UserUpdateRequest(userEmail, "John", "Doe", "1234567890", null);
	final UserEntity user = new UserEntity(null, userEmail, "Password123", "John", "Doe", "1234567890", null, null,
			new HashSet<>(), null, null, null);
	final UserRoleRequest roleRequest = new UserRoleRequest("ADMIN");

	private void mockCurrentUserAuthInfo(String username, boolean isAdmin, boolean isCeo) {
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(username);

		List<GrantedAuthority> authorities = new ArrayList<>();
		if (isAdmin) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		if (isCeo) {
			authorities.add(new SimpleGrantedAuthority("ROLE_CEO"));
		}

		when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	public void testUpdateUser_success() {

		when(userRepository.findByEmail(userUpdateRequest.getEmail())).thenReturn(user);
		UserDataResponse result = accountingManagementService.updateUser(userUpdateRequest);
		verify(userRepository, times(1)).save(user);
		assertEquals("John", result.getName());
		assertEquals("Doe", result.getSurname());
	}

	@Test
	public void testUpdateUser_userNotFound() {
		assertThrows(NoSuchElementException.class, () -> accountingManagementService.updateUser(userUpdateRequest));
	}

	@Test
	public void testUpdateUserCEO_NotAccessAdmin() {
		user.getRoles().add(Role.CEO);
		when(userRepository.findByEmail(userUpdateRequest.getEmail())).thenReturn(user);
		mockCurrentUserAuthInfo(adminEmail, true, false);

		assertThrows(AccessRoleException.class, () -> accountingManagementService.updateUser(userUpdateRequest));
	}

	@Test
	public void testUpdateUser_NotAccessUser() {

		String notAccessUserMail = "user@mail.com";
		UserEntity notAccessUser = new UserEntity();
		notAccessUser.setEmail(notAccessUserMail);
		notAccessUser.setRoles(new HashSet<>());
		when(userRepository.findByEmail(userUpdateRequest.getEmail())).thenReturn(user);
		mockCurrentUserAuthInfo(notAccessUserMail, false, false);
		assertThrows(AccessRoleException.class, () -> accountingManagementService.updateUser(userUpdateRequest));

	}

	@Test
	public void testUpdatePassword_success() {

		when(userRepository.findByEmail(userEmail)).thenReturn(user);

		when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

		Principal principal = () -> userEmail;
		PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword");

		accountingManagementService.updatePassword(principal, request);

		verify(userRepository, times(1)).save(user);
		assertEquals("encodedPassword", user.getHashedPassword());
	}

	@Test
	public void testRemoveUserAda_throwsAccessRoleExceptionForDefaultUser() {
		assertThrows(NoSuchElementException.class, () -> accountingManagementService.removeUser("ada@gmail.com"));
	}

	@Test
	public void testAddRole_success() {
		when(userRepository.findByEmail(userEmail)).thenReturn(user);
		mockCurrentUserAuthInfo(adminEmail, true, false);
		UserDataResponse result = accountingManagementService.addRole(userEmail, roleRequest);

		verify(userRepository, times(1)).save(user);
		assertTrue(user.getRoles().contains(Role.ADMIN));
		assertTrue(result.getRoles().contains(Role.ADMIN));
	}

	@Test
	public void testAddRoleCeo_success() {
		UserRoleRequest seoRole = new UserRoleRequest("CEO");
		when(userRepository.findByEmail(userEmail)).thenReturn(user);
		mockCurrentUserAuthInfo("ceo@mail.com", false, true);
		UserDataResponse result = accountingManagementService.addRole(userEmail, seoRole);

		verify(userRepository, times(1)).save(user);
		assertTrue(user.getRoles().contains(Role.CEO));
		assertTrue(result.getRoles().contains(Role.CEO));
	}

	@Test
	public void testAddRole_userNotFound() {
		String login = "nonexistent@mail.com";
		assertThrows(NoSuchElementException.class, () -> accountingManagementService.addRole(login, roleRequest));
	}

	@Test
	public void testAddRoleYourSelf_NotAccess() {
		when(userRepository.findByEmail(userEmail)).thenReturn(user);
		mockCurrentUserAuthInfo(userEmail, true, true);
		assertThrows(AccessRoleException.class, () -> accountingManagementService.addRole(userEmail, roleRequest));

		mockCurrentUserAuthInfo(userEmail, false, false);
		assertThrows(AccessRoleException.class, () -> accountingManagementService.addRole(userEmail, roleRequest));
	}

	@Test
	public void testAddRole_NotAccess() {
		user.getRoles().add(Role.CEO);
		when(userRepository.findByEmail(userEmail)).thenReturn(user);

		mockCurrentUserAuthInfo(adminEmail, true, false);
		assertThrows(AccessRoleException.class, () -> accountingManagementService.addRole(userEmail, roleRequest));
	}

	@Test
	public void testAddRoleCEO_NotAccessforADMIN() {
		UserRoleRequest seoRole = new UserRoleRequest("CEO");

		when(userRepository.findByEmail(userEmail)).thenReturn(user);

		mockCurrentUserAuthInfo(adminEmail, true, false);
		assertThrows(AccessRoleException.class, () -> accountingManagementService.addRole(userEmail, seoRole));
	}

	@Test
	public void testRemoveRole_success() {
		user.setRoles(new HashSet<>(List.of(Role.ADMIN)));
		when(userRepository.findByEmail(userEmail)).thenReturn(user);
		mockCurrentUserAuthInfo("ceo@mail.com", false, true);

		UserDataResponse result = accountingManagementService.removeRole(userEmail, roleRequest);

		verify(userRepository, times(1)).save(user);
		assertFalse(user.getRoles().contains(Role.ADMIN));
		assertFalse(result.getRoles().contains(Role.ADMIN));
	}

	@Test
	public void testRemoveRole_userNotFound() {
		String login = "nonexistent@mail.com";
		assertThrows(NoSuchElementException.class, () -> accountingManagementService.removeRole(login, roleRequest));
	}

	@Test
	public void testRemoveRoleAdmin_NotAccessAdmin() {
		user.setRoles(new HashSet<>(List.of(Role.ADMIN)));

		when(userRepository.findByEmail(userEmail)).thenReturn(user);

		mockCurrentUserAuthInfo(adminEmail, true, false);

		assertThrows(AccessRoleException.class, () -> accountingManagementService.removeRole(userEmail, roleRequest));
	}
}
