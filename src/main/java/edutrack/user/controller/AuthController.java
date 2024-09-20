package edutrack.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edutrack.security.jwt.JwtUtils;
import edutrack.security.services.RefreshTokenService;
import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public ResponseEntity<UserDataResponse> registerUser(@RequestParam String invite, @RequestBody @Valid UserRegisterRequest userRequest) {
        try {
            UserDataResponse response = authService.registerUser(invite, userRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response){
		return null;
    }

    @PostMapping("/signout")
    @Operation(summary = "User logout", description = "Logs out the user by clearing the JWT cookie.")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!"anonymousUser".equals(authentication.getPrincipal().toString())) {
            String userEmail = authentication.getName(); // get email
            refreshTokenService.deleteByUserEmail(userEmail);               // delete refresh by email
        }

        // Clean JWT and Refresh tokens
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())        // return the same cookie, but without JWT
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString()) // return the same refresh cookie, but without JWT
                .body("You've been signed out!");
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh JWT Token", description = "Generates new JWT access and refresh tokens")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
		return null;
    }
}
