package edutrack.user.service;

import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.UserDataResponse;

public interface AccountService {
	
	 UserDataResponse registerUser(String invite, UserRegisterRequest data);
	/*
	 * CEO able update any user ADMIN able update any user, except another ADMIN and
	 * CEO USER able update only himself so in implementation service we have to
	 * check role of user
	 * 
	 * HERE WE DON'T CHANGE PASSWORD! we have a special another endpoint for this
	 * 
	 * in service :
	 * 
	 * Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); get login from
	 * SecurityContextHolder current user who try to get update: String username =
	 * authentication.getName();
	 * 
	 * and if username != username inside body(whom he want to change) it means he
	 * updates not himself so when we check his role boolean hasAdminRole =
	 * authentication.getAuthorities().stream()
	 * 
	 * .anyMatch(grantedAuthority ->
	 * grantedAuthority.getAuthority().equals("ROLE_ADMIN")); and if hasAdminRole is
	 * not true he can't update if hasAdminRole true, we check person role from body
	 * and if this person exists and doesn't have ADMIN role we finally make update
	 */
	UserDataResponse updateUser(UserUpdateRequest data);

	/*
	 * USER able update only himself ADMIN able update only himself ADMIN can't
	 * change password USER later we add endpoint how to recover password if user
	 * forgets it Principal will have login of user => principal.getName()
	 */
	void updatePassword(String email, PasswordUpdateRequest data);

	/*
	 * access same like updateUser CEO able remove any user, except another CEO
	 * ADMIN able remove any user, except another ADMIN and CEO USER able remove
	 * only himself
	 */
	UserDataResponse removeUser(String email);

	/*
	 * CEO be able to add and remove role for USER and ADMIN be able to add and
	 * remove role for USER ADMIN can't add and remove role for another ADMIN
	 */
	UserDataResponse addRole(String email, UserRoleRequest data);

	UserDataResponse removeRole(String email, UserRoleRequest data);

}
