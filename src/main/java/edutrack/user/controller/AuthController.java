package edutrack.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
    private final AccountService accountService;
//   private final AuthenticationManager authenticationManager;
//    private final JwtUtils jwtUtils;
//    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public ResponseEntity<UserDataResponse> registerUser(@RequestParam String invite, @RequestBody @Valid UserRegisterRequest userRequest) {
            UserDataResponse response = accountService.registerUser(invite, userRequest);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest){
		return null;
    }

    @PostMapping("/signout")
    @Operation(summary = "User logout", description = "Logs out the user by clearing the JWT cookie.")
    public ResponseEntity<?> logoutUser() {
		return null;
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh JWT Token", description = "Generates new JWT access and refresh tokens")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
		return null;
    }
}
