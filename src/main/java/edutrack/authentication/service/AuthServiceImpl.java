package edutrack.authentication.service;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.request.SignOutRequest;
import edutrack.authentication.dto.response.LoginSuccessResponse;
import edutrack.authentication.dto.response.RefreshTokenResponse;
import edutrack.authentication.dto.response.SignOutResponse;
import edutrack.security.jwt.JwtTokenProvider;
import edutrack.security.redis.TokenBlackListService;
import edutrack.user.dto.request.UserRegisterRequest;
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

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    JwtTokenProvider jwtTokenProvider;
    PasswordEncoder passwordEncoder;
    AccountRepository accountRepository;
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
        // 1. check user in DB
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getHashedPassword()))
            throw new AccessException("Invalid email or password");

        // 2. delete old tokens
        if (user.getAccessToken() != null && !tokenBlackListService.isTokenBlacklisted(user.getAccessToken())) {
            tokenBlackListService.addTokenToBlacklist(user.getAccessToken(), "ACCESS_TOKEN");
        }
        if (user.getRefreshToken() != null && !tokenBlackListService.isTokenBlacklisted(user.getRefreshToken())) {
            tokenBlackListService.addTokenToBlacklist(user.getRefreshToken(), "REFRESH_TOKEN");
        }

        // 3. generate new tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 4. check new token in the blacklist
        if (tokenBlackListService.isTokenBlacklisted(accessToken) || tokenBlackListService.isTokenBlacklisted(refreshToken)) {
            throw new AccessException("Generated tokens are blacklisted. Try again.");
        }

        // 5. upd tokens and creation time in the DB
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setTokenCreationTime(Instant.now());
        accountRepository.save(user);

        LoginSuccessResponse loginSuccessResponse = EntityDtoUserMapper.INSTANCE.userToLoginSuccessResponse(user);
        loginSuccessResponse.setAccessToken(accessToken);
        loginSuccessResponse.setRefreshToken(refreshToken);

        return loginSuccessResponse;
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // 1. check the validity of refresh token
        Claims claims;
        try {
            claims = jwtTokenProvider.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        } catch (ExpiredJwtException e) {
            throw new AccessException("Refresh token is expired");
        } catch (Exception e) {
            throw new AccessException("Invalid refresh token");
        }

        // 2. get email from token
        String email = claims.getSubject();

        // 3. check user in the DB and check that the refresh token in the request matches the one stored in the database
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null || !refreshTokenRequest.getRefreshToken().equals(user.getRefreshToken()))
            throw new AccessException("Invalid refresh token");

        // 4. check tokens in the blacklist
        if (tokenBlackListService.isTokenBlacklisted(user.getAccessToken()) ||
                tokenBlackListService.isTokenBlacklisted(refreshTokenRequest.getRefreshToken())) {
            throw new AccessException("Token is blacklisted");
        }

        // 5. if everything OK - upd tokens
        String oldAccessToken = user.getAccessToken();
        String oldRefreshToken = user.getRefreshToken();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        user.setTokenCreationTime(Instant.now());
        accountRepository.save(user);

        // 6. add old tokens to the blacklist
        tokenBlackListService.addTokenToBlacklist(oldAccessToken, "ACCESS_TOKEN");
        tokenBlackListService.addTokenToBlacklist(oldRefreshToken, "REFRESH_TOKEN");

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public SignOutResponse signOutUser(SignOutRequest signOutRequest, String accessToken, String refreshToken) {
        // 1. remove 'Bearer ' from new tokens
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.replace("Bearer ", "");
        }
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.replace("Bearer ", "");
        }

        boolean isAccessTokenExpired = false;

        // 2. check valid tokens
        try {
            jwtTokenProvider.validateAccessToken(accessToken);
        } catch (ExpiredJwtException e) {
            isAccessTokenExpired = true;
            tokenBlackListService.addTokenToBlacklist(accessToken, "ACCESS_TOKEN");
        } catch (Exception e) {
            throw new AccessException("Invalid access token");
        }

        try {
            jwtTokenProvider.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            tokenBlackListService.addTokenToBlacklist(refreshToken, "REFRESH_TOKEN");
        } catch (Exception e) {
            throw new AccessException("Invalid refresh token");
        }

        // 3. check tokens in the blacklist (if one of the tokens is already in the blacklist, continue execution)
        if (!isAccessTokenExpired && tokenBlackListService.isTokenBlacklisted(accessToken)) {
            throw new AccessException("Access token is already blacklisted.");
        }
        if (tokenBlackListService.isTokenBlacklisted(refreshToken)) {
            throw new AccessException("Refresh token is already blacklisted.");
        }

        // 4. add tokens to the blacklist
        if (!isAccessTokenExpired)
            tokenBlackListService.addTokenToBlacklist(accessToken, "ACCESS_TOKEN");
        tokenBlackListService.addTokenToBlacklist(refreshToken, "REFRESH_TOKEN");

        // 5. find user by email
        UserEntity user = accountRepository.findByEmail(signOutRequest.getEmail());
        if (user == null) {
            throw new AccessException("AUTHS. User not found");
        }

        // 6. remove all tokens from Mongo DB
        user.setTokenCreationTime(null);
        user.setAccessToken(null);
        user.setRefreshToken(null);
        accountRepository.save(user);

        return new SignOutResponse("Signed out successfully");
    }
}
