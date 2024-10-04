package edutrack.authentication.service;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.response.LoginSuccessResponse;
import edutrack.authentication.dto.response.RefreshTokenResponse;
import edutrack.authentication.dto.response.SignOutResponse;
import edutrack.security.jwt.JwtTokenProvider;
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
    @Transactional
    public LoginSuccessResponse authenticateUser(String email) {
        // 1. check user in DB
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null)
            throw new AccessException("Invalid email or password");

        // 2. generate new tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 3. upd tokens and creation time in the DB
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
    @Transactional
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

        // 2. find user in the DB and check that the refresh token in the request matches the one stored in the database
        String email = claims.getSubject();
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null || !refreshTokenRequest.getRefreshToken().equals(user.getRefreshToken()))
            throw new AccessException("Invalid refresh token");

        // 4. gen new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        user.setTokenCreationTime(Instant.now());
        accountRepository.save(user);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public SignOutResponse signOutUser(String accessToken) {
        // 1. remove 'Bearer ' from token
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.replace("Bearer ", "");
        }

        // 2. Verify access token
        String email;
        try {
            Claims accessClaims = jwtTokenProvider.validateAccessToken(accessToken);
            email = accessClaims.getSubject();
        } catch (ExpiredJwtException e) {
            email = e.getClaims().getSubject();
        } catch (Exception e) {
            throw new AccessException("Unable to extract email from access token");
        }

        // 3. find user by email
        UserEntity user = accountRepository.findByEmail(email);
        if (user == null) {
            throw new AccessException("User not found");
        }

        // 5. remove all tokens from Mongo DB
        user.setTokenCreationTime(null);
        user.setAccessToken(null);
        user.setRefreshToken(null);
        accountRepository.save(user);

        return new SignOutResponse("Signed out successfully");
    }
}
