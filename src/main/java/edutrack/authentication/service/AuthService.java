package edutrack.authentication.service;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.request.SignOutRequest;
import edutrack.authentication.dto.responce.RefreshTokenResponse;
import edutrack.authentication.dto.responce.SignOutResponse;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.authentication.dto.responce.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;

public interface AuthService {
    UserDataResponse registerUser(String invite, UserRegisterRequest data);
    LoginSuccessResponse authenticateUser(String email, String password);
    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    SignOutResponse signOutUser(SignOutRequest signOutRequest, String accessToken, String refreshToken);
}
