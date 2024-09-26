package edutrack.authentication.controller;

import edutrack.authentication.dto.request.LoginRequest;
import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.request.SignOutRequest;
import edutrack.authentication.dto.response.LoginSuccessResponse;
import edutrack.authentication.dto.response.RefreshTokenResponse;
import edutrack.authentication.dto.response.SignOutResponse;
import edutrack.authentication.service.AuthService;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.UserDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public UserDataResponse registerUser(@RequestParam String invite,
                                         @RequestBody @Valid UserRegisterRequest userRequest) {
        return authService.registerUser(invite, userRequest);
    }

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public LoginSuccessResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh access and refresh tokens", description = "Refreshes an access and refresh tokens using a valid refresh token.")
    public RefreshTokenResponse authenticateUser(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/signout")
    @Operation(summary = "Sign out user", description = "Signs out the user.")
    public SignOutResponse signOutUser(@RequestBody SignOutRequest signOutRequest,
                                       @RequestHeader("Authorization") String accessToken,
                                       @RequestHeader("Refresh-Token") String refreshToken) {
        return authService.signOutUser(signOutRequest, accessToken, refreshToken);
    }
}
