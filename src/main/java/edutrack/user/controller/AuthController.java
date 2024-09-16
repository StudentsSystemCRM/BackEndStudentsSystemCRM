package edutrack.user.controller;

import edutrack.security.jwt.JwtUtils;
import edutrack.security.services.UserDetailsImpl;
import edutrack.user.dto.request.LoginRequest;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/auth/signup")
    @Operation(summary = "Register a new user", description = "Registers a new user using an invite code and user details.")
    public LoginSuccessResponse registerUser(
            @RequestParam String invite,
            @RequestBody @Valid UserRegisterRequest userRequest) {
        return authService.registration(invite, userRequest);
    }

    @PostMapping("/auth/signin")
    @Operation(summary = "User login", description = "Logs in the user and returns a token for authentication.")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response){
        // auth user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // get info about user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // generate  JWT cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // Entity mapping to DTO and added token to response
        LoginSuccessResponse loginResponse = authService.loginByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        loginResponse.setToken(jwtUtils.generateTokenFromUsername(userDetails.getUsername()));

        return ResponseEntity.ok(loginResponse);
    }
}
