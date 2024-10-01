package edutrack.authentication.service;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.response.LoginSuccessResponse;
import edutrack.authentication.dto.response.RefreshTokenResponse;
import edutrack.authentication.dto.response.SignOutResponse;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.UserDataResponse;

public interface AuthService {
    UserDataResponse registerUser(String invite, UserRegisterRequest data);
    LoginSuccessResponse authenticateUser(String email);
    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    SignOutResponse signOutUser(String accessToken, String refreshToken);
}