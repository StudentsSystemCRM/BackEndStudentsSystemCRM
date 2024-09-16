package edutrack.user.service;

import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;

public interface AuthService {
    LoginSuccessResponse registration(String invite, UserRegisterRequest data);

    LoginSuccessResponse loginByEmailAndPassword(String email, String password);
}
