package edutrack.service;

import java.security.Principal;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;

/*
 * every http  request to our server,except endpoint registration 
 * must have header field 
 * Authorization login : password in base64
 * or  
 * Authorization jwt token,  we will add jwt security later 
 */
public interface IAccountingManagement {

	/*
	 * permit all, doesn't need header authorization later we will generate invite
	 * and save it in DB when somebody will want to registered we will check this
	 * invite in service and get new user role(it depends on invite)
	 */
	LoginSuccessResponse registration(String invite, UserRegisterRequest data);

	/*
	 * http header has to have field Authorization login : password in base64 when
	 * we will add security we realize PasswordEncoder(default bcrypt) and
	 * UserDetailsService and security will check automatically password and if hash
	 * password from mongo equals password in header our service will be able give
	 * access to user and return the json token inside UserDataResponse, example
	 * response: {... "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 ... } or
	 * response status 401 if not equals "
	 * 
	 * user.getName() => give the login and get from DB all info for LoginSuccessResponse
	 */
	LoginSuccessResponse login(Principal user);

	/*
	 * CEO able update any user ADMIN able update any user, except another ADMIN and
	 * CEO USER able update only himself so in implementation service we have to
	 * check role of user
	 * 
	 * HERE WE DONT CHANGE PASSWORD! we have a special another endpoint for this
	 * 
	 * in service :
	 * 
	 * Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); get login from
	 * SecurityContextHolder current user who try get update: String username =
	 * authentication.getName();
	 * 
	 * and if username != username inside body(whom he want to change) it means he
	 * update not himself so when we check his role boolean hasAdminRole =
	 * authentication.getAuthorities().stream()
	 * 
	 * .anyMatch(grantedAuthority ->
	 * grantedAuthority.getAuthority().equals("ROLE_ADMIN")); and if hasAdminRole is
	 * not true he can't update if hasAdminRole true, we check person role from body
	 * and if this person exists and doen't have ADMIN role we finally make update
	 */
	UserDataResponse updateUser(UserUpdateRequest data);

	/*
	 * USER able update only himself ADMIN able update only himself ADMIN can't
	 * change password USER later we add endpoint how to recover password if user
	 * forgets it Principal will has login of user => principal.getName()
	 */
	void updatePassword(Principal user, PasswordUpdateRequest data);

	/*
	 * access same like updateUser CEO able remove any user, except another CEO
	 * ADMIN able remove any user, except another ADMIN and CEO USER able remove
	 * only himself
	 */
	UserDataResponse removeUser(String login);
	

	/*
	 * CEO be able add and remove role for USER and ADMIN ADMIN be able add and
	 * remove role for USER ADMIN can't add and remove role for another ADMIN
	 */
	UserDataResponse addRole(String login, UserRoleRequest data);

	UserDataResponse removeRole(String login, UserRoleRequest data);

}
