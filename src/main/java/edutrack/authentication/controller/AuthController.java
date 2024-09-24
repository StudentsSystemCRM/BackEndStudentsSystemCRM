package edutrack.authentication.controller;

import edutrack.authentication.service.AuthService;
import edutrack.authentication.dto.request.LoginRequest;
import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.authentication.dto.responce.LoginSuccessResponse;
import edutrack.authentication.dto.responce.RefreshTokenResponse;
import edutrack.user.dto.response.UserDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public UserDataResponse registerUser(@RequestParam String invite, @RequestBody @Valid UserRegisterRequest userRequest) {
        return authService.registerUser(invite, userRequest);
    }

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public LoginSuccessResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a valid refresh token.")
    public RefreshTokenResponse authenticateUser(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

//    @PostMapping("/signout")
//    @Operation(summary = "Sign out user", description = "Signs out the user by invalidating the refresh token.")
//    public SignOutResponse signOutUser(@RequestBody SignOutRequest signOutRequest) {
//
//    }
}
