package edutrack.controller;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.service.IAccountingManagement;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserAccountingController {
    private final IAccountingManagement accountingService;
    @Autowired
    public UserAccountingController(IAccountingManagement accountingService) {
        this.accountingService = accountingService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public LoginSuccessResponse registerUser(
    		@RequestParam String invite,
    		@RequestBody @Valid UserRegisterRequest userRequest) {
        return accountingService.registration(invite, userRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public LoginSuccessResponse loginUser(Principal principal) {
        return accountingService.login(principal);
    }

    @PutMapping("/update")
    @Operation(summary = "Update user information", description = "Updates the user's profile information. Note: Password should be updated separately.")
    public UserDataResponse updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return accountingService.updateUser(userUpdateRequest);
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update user password", description = "Updates the user's password. This operation is only allowed for the user themselves.")
    public void updatePassword(Principal principal, @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        accountingService.updatePassword(principal, passwordUpdateRequest);
    }

    @PutMapping("/assign-role/{login}")
    @Operation(summary = "Assign role to user", description = "Assigns a role to the specified user. Note: Only admins can assign roles.")
    public UserDataResponse assignRole(
    		@PathVariable @NotNull @Email String login,
    		@RequestBody @Valid UserRoleRequest userRoleRequest) {
        return accountingService.addRole(login, userRoleRequest);
    }

    @DeleteMapping("/remove-role/{login}")
    @Operation(summary = "Remove role from user", description = "Removes a role from the specified user. Note: Only admins can remove roles.")
    public UserDataResponse removeRole(
    		@PathVariable @NotNull @Email String login, 
    		@RequestBody @Valid UserRoleRequest userRoleRequest) {
        return accountingService.removeRole(login, userRoleRequest);
    }

    @DeleteMapping("/{login}")
    @Operation(summary = "Remove user", description = "Deletes the specified user from the system. Note: Only admins can remove users.")
    public UserDataResponse removeUser(@PathVariable @NotNull @Email String login) {
        return accountingService.removeUser(login);
    }
}
