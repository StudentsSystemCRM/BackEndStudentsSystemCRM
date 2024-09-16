package edutrack.user.controller;

import edutrack.security.jwt.AuthTokenFilter;
import edutrack.security.jwt.JwtUtils;
import edutrack.security.services.UserDetailsImpl;
import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @PostMapping("/auth/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public ResponseEntity<UserDataResponse> registerUser(
            @RequestParam String invite,
            @RequestBody @Valid UserRegisterRequest userRequest) {
        try {
            UserDataResponse response = authService.registerUser(invite, userRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response){
        logger.info("User attempting to log in with email: {}", loginRequest.getEmail());

        // auth user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // get info about user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        logger.info("User '{}' logged in successfully", userDetails.getUsername());

        // generate  JWT cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        logger.info("JWT token added to response for user '{}'", userDetails.getUsername());

        // Entity mapping to DTO and added token to response
        LoginSuccessResponse loginResponse = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        loginResponse.setToken(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signout")
    @Operation(summary = "User logout", description = "Logs out the user by clearing the JWT cookie.")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }
}
