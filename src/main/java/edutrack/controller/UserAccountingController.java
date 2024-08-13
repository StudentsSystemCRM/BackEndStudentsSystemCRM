package edutrack.controller;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.service.IAccountingManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserAccountingController {

    @Autowired
    private IAccountingManagement accountingService;

    @PostMapping("/register")
    public LoginSuccessResponse registerUser(@RequestParam String invite, @RequestBody UserRegisterRequest userRequest) {
        return accountingService.registration(invite, userRequest);
    }

    @PostMapping("/login")
    public LoginSuccessResponse loginUser(Principal principal) {
        return accountingService.login(principal);
    }

    @PutMapping("/update")
    public UserDataResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        return accountingService.updateUser(userUpdateRequest);
    }

    @PutMapping("/update-password")
    public void updatePassword(Principal principal, @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        accountingService.updatePassword(principal, passwordUpdateRequest);
    }

    @PutMapping("/assign-role/{login}")
    public UserDataResponse assignRole(@PathVariable String login, @RequestBody UserRoleRequest userRoleRequest) {
        return accountingService.addRole(login, userRoleRequest);
    }

    @DeleteMapping("/remove-role/{login}")
    public UserDataResponse removeRole(@PathVariable String login, @RequestBody UserRoleRequest userRoleRequest) {
        return accountingService.removeRole(login, userRoleRequest);
    }

    @DeleteMapping("/{login}")
    public UserDataResponse removeUser(@PathVariable String login) {
        return accountingService.removeUser(login);
    }
}
