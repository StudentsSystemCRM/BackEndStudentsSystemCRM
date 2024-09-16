package edutrack.user.service;

import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;

public interface AuthService {
    UserDataResponse registerUser(String invite, UserRegisterRequest data);

    LoginSuccessResponse authenticateUser(String email, String password);
}
