package edutrack.user.controller;

import edutrack.security.jwt.JwtUtils;
import edutrack.security.services.UserDetailsImpl;
import edutrack.user.dto.request.*;
import edutrack.user.service.AccountService;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
	private final JwtUtils jwtUtils;
    private final AccountService accountingService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/auth/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public LoginSuccessResponse registerUser(
    		@RequestParam String invite,
    		@RequestBody @Valid UserRegisterRequest userRequest) {
        return accountingService.registration(invite, userRequest);
    }

    @PostMapping("/auth/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response){
        // auth user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // get info about user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // generate  JWT cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // Entity mapping to DTO and added token to response
        LoginSuccessResponse loginResponse = accountingService.loginByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        loginResponse.setToken(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));

        return ResponseEntity.ok(loginResponse);
    }

















    @PutMapping("/users/update")
    @Operation(summary = "Update user information", description = "Updates the user's profile information. Note: Password should be updated separately.")
    public UserDataResponse updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return accountingService.updateUser(userUpdateRequest);
    }

    @PutMapping("/users/update-password")
    @Operation(summary = "Update user password", description = "Updates the user's password. This operation is only allowed for the user themselves.")
    public void updatePassword(Principal principal, @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        accountingService.updatePassword(principal, passwordUpdateRequest);
    }

    @PutMapping("/users/assign-role/{login}")
    @Operation(summary = "Assign role to user", description = "Assigns a role to the specified user. Note: Only admins can assign roles.")
    public UserDataResponse assignRole(
    		@PathVariable @NotNull @Email String login,
    		@RequestBody @Valid UserRoleRequest userRoleRequest) {
        return accountingService.addRole(login, userRoleRequest);
    }

    @DeleteMapping("/users/remove-role/{login}")
    @Operation(summary = "Remove role from user", description = "Removes a role from the specified user. Note: Only admins can remove roles.")
    public UserDataResponse removeRole(
    		@PathVariable @NotNull @Email String login, 
    		@RequestBody @Valid UserRoleRequest userRoleRequest) {
        return accountingService.removeRole(login, userRoleRequest);
    }

    @DeleteMapping("/users/{login}")
    @Operation(summary = "Remove user", description = "Deletes the specified user from the system. Note: Only admins can remove users.")
    public UserDataResponse removeUser(@PathVariable @NotNull @Email String login) {
        return accountingService.removeUser(login);
    }
}
