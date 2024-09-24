package edutrack.authentication.service;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.request.SignOutRequest;
import edutrack.authentication.dto.responce.RefreshTokenResponse;
import edutrack.authentication.dto.responce.SignOutResponse;
import edutrack.security.jwt.JwtService;
import edutrack.security.jwt.redis.TokenBlackListService;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.authentication.dto.responce.LoginSuccessResponse;
import edutrack.user.dto.response.Role;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.ResourceExistsException;
import edutrack.user.repository.AccountRepository;
import edutrack.user.util.EntityDtoUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements edutrack.authentication.service.AuthService {
    PasswordEncoder passwordEncoder;
    AccountRepository accountRepository;
    JwtService jwtService;
    TokenBlackListService tokenBlackListService;

    @Override
    @Transactional
    public UserDataResponse registerUser(String invite, UserRegisterRequest data) {
        //TODO  invite check invite

        UserEntity existingUser = accountRepository.findByEmail(data.getEmail());

        if (existingUser != null)
            throw new ResourceExistsException("user with email " + data.getEmail() + "already exists");

        UserEntity newUser = EntityDtoUserMapper.INSTANCE.userRegisterRequestToUser(data);
        newUser.setRoles(new HashSet<>(List.of(Role.USER)));
        newUser.setHashedPassword(passwordEncoder.encode(data.getPassword()));
        accountRepository.save(newUser);

        return EntityDtoUserMapper.INSTANCE.userToUserDataResponse(newUser);
    }

    @Override
    public LoginSuccessResponse authenticateUser(String email, String password) {
        UserEntity user = accountRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getHashedPassword()))
            throw new AccessException("Invalid email or password");

        String token = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        LoginSuccessResponse response = EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(user);
        response.setToken(token);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        Claims claims = jwtService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String email = claims.getSubject();
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null)
            throw new AccessException("Invalid refresh token");

        String newAccessToken = jwtService.generateAccessToken(user);
        return new RefreshTokenResponse(newAccessToken);
    }

    @Override
    public SignOutResponse signOutUser(SignOutRequest signOutRequest, String accessToken, String refreshToken) {
        // if access token exist, add it to blacklist, if it expired
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                String token = accessToken.substring(7);

                // get expired time access token and add it to black list
                long accessTokenExpiration = jwtService.getExpirationTime(token);
                tokenBlackListService.addToBlackList(token, accessTokenExpiration);
            } catch (ExpiredJwtException ex) {
                // token expired - add to black list
                String token = accessToken.substring(7);
                tokenBlackListService.addToBlackList(token, 0); // TTL = 0, immediately remove
            }
        }

        // if refresh token exist - add to blacklist
        if (refreshToken != null && !refreshToken.isEmpty()) {
            long refreshTokenExpiration = jwtService.getExpirationTime(refreshToken);
            tokenBlackListService.addToBlackList(refreshToken, refreshTokenExpiration);
        }

        return new SignOutResponse("Signed out successfully");
    }

}
