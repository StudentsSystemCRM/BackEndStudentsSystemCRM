package edutrack.user.controller;

import edutrack.security.entity.RefreshTokenEntity;
import edutrack.security.jwt.AuthTokenFilter;
import edutrack.security.jwt.JwtUtils;
import edutrack.security.services.RefreshTokenService;
import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

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
        logger.info("User attempting to log in with email: {}", loginRequest.getEmail());

        // auth user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // get info about user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        logger.info("User '{}' logged in successfully", userDetails.getUsername()); // username = email

        // generate JWT access token after and add to cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails); // generate cookie (with JWT)
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());   // HttpCookie packing for storage

        // create refresh token and add to cookie
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());    // username = email
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        logger.info("JWT and Refresh tokens added to response for user '{}'", userDetails.getUsername());

        // Entity mapping to DTO and added token to response
        LoginSuccessResponse loginResponse = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        loginResponse.setToken(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));

        return ResponseEntity.ok(loginResponse);
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
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);;
;
        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(tokenEntity -> {
                        String userEmail = tokenEntity.getUserEmail();
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userEmail);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body("Token is refreshed successfully!");
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body("Refresh Token is empty!");
    }
}
