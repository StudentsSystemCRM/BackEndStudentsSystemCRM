package edutrack.service;

import edutrack.dto.request.accounting.PasswordUpdateRequest;
import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.accounting.UserRoleRequest;
import edutrack.dto.request.accounting.UserUpdateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AccountingManagementService implements IAccountingManagement{

    @Override
    public LoginSuccessResponse registration(String invite, UserRegisterRequest data) {
        return null;
    }

    @Override
    public LoginSuccessResponse login(Principal user) {
        return null;
    }

    @Override
    public UserDataResponse updateUser(UserUpdateRequest data) {
        return null;
    }

    @Override
    public void updatePassword(Principal user, PasswordUpdateRequest data) {

    }

    @Override
    public UserDataResponse removeUser(String login) {
        return null;
    }

    @Override
    public UserDataResponse addRole(String login, UserRoleRequest data) {
        return null;
    }

    @Override
    public UserDataResponse removeRole(String login, UserRoleRequest data) {
        return null;
    }
}
