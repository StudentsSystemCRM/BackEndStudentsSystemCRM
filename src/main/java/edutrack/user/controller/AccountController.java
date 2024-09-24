package edutrack.user.controller;

import edutrack.user.service.AccountService;
import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.UserDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountingService;

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