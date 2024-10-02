package edutrack.auth;

import edutrack.authentication.dto.request.RefreshTokenRequest;
import edutrack.authentication.dto.response.LoginSuccessResponse;
import edutrack.authentication.dto.response.RefreshTokenResponse;
import edutrack.authentication.dto.response.SignOutResponse;
import edutrack.authentication.service.AuthServiceImpl;
import edutrack.security.jwt.JwtTokenProvider;
import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.ResourceExistsException;
import edutrack.user.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static com.mongodb.assertions.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImpTests {
    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    String userEmail = "test@mail.com";
    UserRegisterRequest userRegisterRequest = new UserRegisterRequest(userEmail, "Password123!", "John", "Doe", "1234567890", null);
    UserEntity user = new UserEntity(null, userEmail, "Password123", "John", "Doe", "1234567890", null, null, new HashSet<>(), null, null, null);

    @Test
    public void testRegistration_success() {
        // Mock the repository response for findByEmail (returning null, user doesn't exist)
        when(accountRepository.findByEmail(userEmail)).thenReturn(null);
        when(passwordEncoder.encode(userRegisterRequest.getPassword())).thenReturn("hashedPassword");

        // Perform the registration
        UserDataResponse result = authService.registerUser("invite", userRegisterRequest);

        // Verify save method was called
        verify(accountRepository, times(1)).save(any(UserEntity.class));

        // Check result
        assertEquals(userEmail, result.getEmail());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
    }

    @Test
    public void testRegistration_failure() {
        // Mock an exception to be thrown when saving a new user
        when(accountRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Mocked exception"));

        // Ensure that the exception is thrown during registration
        RuntimeException result = assertThrows(RuntimeException.class, () -> authService.registerUser("invite", userRegisterRequest));

        assertEquals("Mocked exception", result.getMessage());
        verify(accountRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testRegistration_userAlreadyExists() {
        // Mock the repository response for findByEmail (returning an existing user)
        when(accountRepository.findByEmail(userEmail)).thenReturn(user);

        // Test exception
        assertThrows(ResourceExistsException.class, () -> authService.registerUser("invite", userRegisterRequest));
    }

    @Test
    public void testRegistration_saveFailure() {
        // Mock the repository response to throw an exception when saving
        when(accountRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Database error"));

        // Test that exception is thrown
        assertThrows(RuntimeException.class, () -> authService.registerUser("invite", userRegisterRequest));
    }

    @Test
    public void testAuthenticateUser_success() {
        // Mock repository to return a valid user
        when(accountRepository.findByEmail(userEmail)).thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn("refreshToken");

        // Perform authentication
        LoginSuccessResponse result = authService.authenticateUser(userEmail);

        // Verify repository and token generation calls
        verify(accountRepository, times(1)).findByEmail(userEmail);
        verify(jwtTokenProvider, times(1)).generateAccessToken(user);
        verify(jwtTokenProvider, times(1)).generateRefreshToken(user);

        // Check result
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    public void testAuthenticateUser_userNotFound() {
        // Mock repository to return null (user not found)
        when(accountRepository.findByEmail(userEmail)).thenReturn(null);

        // Test that exception is thrown
        assertThrows(AccessException.class, () -> authService.authenticateUser(userEmail));
    }

    @Test
    public void testRefreshToken_success() {
        // create mock obj Claims
        Claims claims = mock(Claims.class);

        // Mock UserEntity object
        UserEntity mockUser = mock(UserEntity.class);

        // Mock method behavior
        when(jwtTokenProvider.validateRefreshToken("validToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userEmail);
        when(accountRepository.findByEmail(userEmail)).thenReturn(mockUser); // Используем мок
        when(mockUser.getRefreshToken()).thenReturn("validToken");  // Мокируем refresh-токен пользователя
        when(jwtTokenProvider.generateAccessToken(mockUser)).thenReturn("newAccessToken");
        when(jwtTokenProvider.generateRefreshToken(mockUser)).thenReturn("newRefreshToken");

        // Perform token update operation
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("validToken");
        RefreshTokenResponse result = authService.refreshToken(refreshTokenRequest);

        // Check that the methods have been called the required number of times
        verify(accountRepository, times(1)).findByEmail(userEmail);
        verify(jwtTokenProvider, times(1)).generateAccessToken(mockUser);
        verify(jwtTokenProvider, times(1)).generateRefreshToken(mockUser);

        // Check result
        assertEquals("newAccessToken", result.getAccessToken());
        assertEquals("newRefreshToken", result.getRefreshToken());
    }

    @Test
    public void testRefreshToken_invalidToken() {
        // Mock invalid token validation
        when(jwtTokenProvider.validateRefreshToken("invalidToken")).thenThrow(new AccessException("Invalid token"));

        // Test that exception is thrown
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalidToken");
        assertThrows(AccessException.class, () -> authService.refreshToken(refreshTokenRequest));
    }

    @Test
    public void testSignOutUser_success() {
        // create mock obj Claims
        Claims claims = mock(Claims.class);

        // mock methods
        when(jwtTokenProvider.validateAccessToken("validAccessToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(userEmail);
        when(accountRepository.findByEmail(userEmail)).thenReturn(user);

        // Execute sign out
        SignOutResponse result = authService.signOutUser("Bearer validAccessToken");

        // Verify that the tokens have been cleared and the user has been saved
        verify(accountRepository, times(1)).save(user);
        assertNull(user.getAccessToken());
        assertNull(user.getRefreshToken());

        assertEquals("Signed out successfully", result.getMessage());
    }

    @Test
    public void testSignOutUser_userNotFound() {
        lenient().when(accountRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(AccessException.class, () -> authService.signOutUser("Bearer validAccessToken"));
    }
}

//    final UserRegisterRequest USER_REGISTER_REQUEST = UserRegisterRequest.builder()
//            .email(VALID_USER_EMAIL_1)
//            .password(VALID_USER_PASSWORD_1)
//            .name(VALID_USER_NAME_1)
//            .surname(VALID_USER_SURNAME_1)
//            .phone(VALID_USER_PHONE_1)
//            .birthdate(VALID_BIRTHDATE)
//            .build();
//    final LoginSuccessResponse LOGIN_SUCCESS_RESPONSE = LoginSuccessResponse.builder()
//            .token(VALID_TOKEN)
//            .name(VALID_USER_NAME_1)
//            .surname(VALID_USER_SURNAME_1)
//            .phone(VALID_USER_PHONE_1)
//            .birthdate(VALID_BIRTHDATE)
//            .createdDate(VALID_CREATED_DATE)
//            .roles(VALID_ROLE)
//            .build();

//    @Test
//    @SneakyThrows
//    @DisplayName("Create user, valid input")
//    void testRegisterUser_whenValidUserRegisterRequestIsProvided_thenReturnLoginSuccessResponse() {
//        //Arrange
//        UserRegisterRequest requestData = USER_REGISTER_REQUEST;
//        LoginSuccessResponse expectedData = LOGIN_SUCCESS_RESPONSE;
//        String invite = "empty";
//        String expectedJson = objectMapper.writeValueAsString(expectedData);
//        String jsonRequestData = objectMapper.writeValueAsString(requestData);
//
//        when(accountingManagementService.registration(invite, requestData)).thenReturn(expectedData);
//
//        //Act
//        ResultActions resultActions = mockMvc.perform(post("/api/users/register")
//                .param("invite", invite)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonRequestData));
//        //Assert
//        resultActions.andExpect(status().isOk())
//                .andExpect(result -> assertEquals(result.getResponse().getContentAsString(), expectedJson));
//        verify(accountingManagementService, times(1)).registration(any(String.class), any(UserRegisterRequest.class));
//
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Create user, duplicate id")
//    void testCreateUser_whenUserWithDuplicateIdIsProvided_thenReturnsBadRequest() {
//        UserRegisterRequest requestData = USER_REGISTER_REQUEST;
//        String jsonRequestData = objectMapper.writeValueAsString(requestData);
//        String invite = "empty";
//        String expectedErrorMessage = "user with email " + requestData.getEmail() + "already exists";
//        when(accountingManagementService.registration(invite, requestData))
//                .thenThrow(new ResourceExistsException(expectedErrorMessage));
//        //Act
//        ResultActions resultActions = mockMvc.perform(post("/api/users/register")
//                .param("invite", invite)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonRequestData));
//
//        //Asserts
//        resultActions.andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Create user, invalid password")
//    void testCreateUser_whenUserWithNotValidPasswordIsProvided_thenReturnBadRequest() {
//        //Arrange
//        UserRegisterRequest requestData = USER_REGISTER_REQUEST
//                .withPassword("12345").withName("123").withEmail("wrongEmail");
//        String jsonRequestData = objectMapper.writeValueAsString(requestData);
//        String invite = "empty";
//        String expectedErrorMessage1 = ValidationAccountingMessage.INVALID_PASSWORD_CONTAIN;
//        String expectedErrorMessage2 = ValidationAccountingMessage.INVALID_NAME;
//        String expectedErrorMessage3 = ValidationAccountingMessage.INVALID_EMAIL;
//
//        //Act
//        ResultActions resultActions = mockMvc.perform(post("/api/users/register")
//                .param("invite", invite)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonRequestData));
//
//        //Assert
//        resultActions.andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").isArray())
//                .andExpect(jsonPath("$.message", hasSize(3)))
//                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage1)))
//                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage2)))
//                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage3)));
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Login, valid input")
//    @WithMockUser
//    void testLogin_whenValidInputIsProvided_thenReturnLoginSuccessResponse() {
//        //Arrange
//        LoginSuccessResponse expectedResponse = LOGIN_SUCCESS_RESPONSE;
//        String jsonLoginSuccessResponse = objectMapper.writeValueAsString(expectedResponse);
//        when(accountingManagementService.login(any(Principal.class))).thenReturn(expectedResponse);
//
//        //Act
//        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
//                .contentType(MediaType.APPLICATION_JSON));
//
//        //Assert
//        resultActions.andExpect(status().isOk()).
//                andExpect(result -> assertEquals(jsonLoginSuccessResponse, result.getResponse().getContentAsString()));
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("Login, not authorized")
//    void testLogin_whenValidInputIsNotProvided_thenReturnUnauthorized() {
//        when(accountingManagementService.login(any(Principal.class))).thenReturn(LOGIN_SUCCESS_RESPONSE);
//        //Act
//        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
//                .contentType(MediaType.APPLICATION_JSON));
//        //Assert
//        resultActions.andExpect(status().isUnauthorized()).
//                andExpect(result -> assertEquals("Unauthorized", result.getResponse().getErrorMessage()));
//        verify(accountingManagementService, times(0)).login(any(Principal.class));
//    }
