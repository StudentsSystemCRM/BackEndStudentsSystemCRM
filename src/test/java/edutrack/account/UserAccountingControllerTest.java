package edutrack.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.security.jwt.JwtRequestFilter;
import edutrack.user.constant.ValidationAccountingMessage;
import edutrack.user.controller.AccountController;
import edutrack.user.dto.request.PasswordUpdateRequest;
import edutrack.user.dto.request.UserRoleRequest;
import edutrack.user.dto.request.UserUpdateRequest;
import edutrack.user.dto.response.Role;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.dto.validation.ValidRangeDate;
import edutrack.user.dto.validation.ValidRole;
import edutrack.user.exception.AccessException;
import edutrack.user.service.AccountServiceImp;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JacksonAutoConfiguration.class})
public class UserAccountingControllerTest {
    @MockBean
    AccountServiceImp accountingManagementService;

    @MockBean
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // valid userRegisterRequest test data
    final String VALID_USER_EMAIL_1 = "user1@test.com";
    final String VALID_USER_PASSWORD_1 = "12345Qwe=123";
    final String VALID_USER_NAME_1 = "User";
    final String VALID_USER_SURNAME_1 = "Userov";
    final String VALID_USER_PHONE_1 = "+123-456-78(9123)";

    // valid loginSuccessResponse test data
    final LocalDate VALID_BIRTHDATE = LocalDate.of(2000, 12, 15);
    final LocalDate VALID_CREATED_DATE = LocalDate.of(2024, 8, 8);
    final Set<Role> VALID_ROLE = Collections.singleton(Role.USER);

    final UserUpdateRequest USER_UPDATE_REQUEST = UserUpdateRequest.builder()
            .email(VALID_USER_EMAIL_1)
            .name(VALID_USER_NAME_1)
            .surname(VALID_USER_SURNAME_1)
            .phone(VALID_USER_PHONE_1)
            .birthdate(VALID_BIRTHDATE)
            .build();

    final UserDataResponse USER_DATA_RESPONSE = UserDataResponse.builder()
            .email(VALID_USER_EMAIL_1)
            .name(VALID_USER_NAME_1)
            .surname(VALID_USER_SURNAME_1)
            .phone(VALID_USER_PHONE_1)
            .birthdate(VALID_BIRTHDATE)
            .createdDate(VALID_CREATED_DATE)
            .roles(VALID_ROLE)
            .build();

    final PasswordUpdateRequest PASSWORD_UPDATE_REQUEST = new PasswordUpdateRequest(VALID_USER_PASSWORD_1);
    final UserRoleRequest USER_ROLE_REQUEST = new UserRoleRequest("USER");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    @DisplayName("Update, valid input")
    void testUpdate_whenValidInputIsProvided_thenReturnUserDataResponse() {
        //Arrange
        String requestDataJson = objectMapper.writeValueAsString(USER_UPDATE_REQUEST);
        UserDataResponse expectedData = USER_DATA_RESPONSE;
        String expectedDataJson = objectMapper.writeValueAsString(expectedData);

        when(accountingManagementService.updateUser(any(UserUpdateRequest.class))).thenReturn(expectedData);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update")
                .with(user("user").password("123").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(result -> assertEquals(expectedDataJson, result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @DisplayName("Update, invalid input")
    void testUpdate_whenInvalidInputIsProvided_thenReturnBadRequest() {
        //Arrange
        UserUpdateRequest requestData = USER_UPDATE_REQUEST
                .withBirthdate(LocalDate.of(2026, 1, 1)).withSurname("userov.surname");
        String requestDataJson = objectMapper.writeValueAsString(requestData);
        String expectedErrorMessage1 = ValidationAccountingMessage.INVALID_NAME;

        Field birthdateField = UserUpdateRequest.class.getDeclaredField("birthdate");
        ValidRangeDate annotation = birthdateField.getAnnotation(ValidRangeDate.class);
        String expectedErrorMessage2 = annotation.message();

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message", hasSize(2)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage1)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage2)));

        verify(accountingManagementService, times(0)).updateUser(any(UserUpdateRequest.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @DisplayName("Update, no permission")
    void testUpdate_whenValidInputIsProvided_thenThrowNoPermissionAndReturnForbidden() {
        //Arrange
        String requestDataJson = objectMapper.writeValueAsString(USER_UPDATE_REQUEST);
        String expectedErrorMessage1 = "You don't have rules to update this user's profile.";

        when(accountingManagementService.updateUser(any(UserUpdateRequest.class))).thenThrow(new AccessException(expectedErrorMessage1));

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));

        //Assert
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage1));

        verify(accountingManagementService, times(1)).updateUser(any(UserUpdateRequest.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @DisplayName("Update, no such user")
    void testUpdate_whenValidInputIsProvided_thenThrowNoSuchElementExceptionAndReturnBadRequest() {
        //Arrange
        String requestDataJson = objectMapper.writeValueAsString(USER_UPDATE_REQUEST);
        String expectedErrorMessage1 = "Account with email '%s' not found".formatted(USER_UPDATE_REQUEST.getEmail());

        when(accountingManagementService.updateUser(any(UserUpdateRequest.class))).thenThrow(new NoSuchElementException(expectedErrorMessage1));

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));

        //Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("An unexpected error: " + expectedErrorMessage1));

        verify(accountingManagementService, times(1)).updateUser(any(UserUpdateRequest.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdatePassword, valid input")
    @WithMockUser
    void testUpdatePassword_whenValidPasswordUpdateRequestIsProvided_thenReturn200() {
        //Arrange
        String requestDataJson = objectMapper.writeValueAsString(PASSWORD_UPDATE_REQUEST);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        // Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update-password")
                .contentType("application/json")
                .content(requestDataJson));

        // Assert
        resultActions.andExpect(status().isOk());
        verify(accountingManagementService).updatePassword(eq(principal), eq(PASSWORD_UPDATE_REQUEST));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdatePassword, invalid input")
    @WithMockUser
    void testUpdatePassword_whenInvalidPasswordUpdateRequestIsProvided_thenReturnBadRequest() {
        //Arrange
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(null);
        String requestDataJson = objectMapper.writeValueAsString(passwordUpdateRequest);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update-password")
                .contentType("application/json")
                .content(requestDataJson));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ValidationAccountingMessage.NULL_PASSWORD));
        verify(accountingManagementService, never()).updatePassword(any(Principal.class), any(PasswordUpdateRequest.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdatePassword, incorrect body")
    @WithMockUser
    void testUpdatePassword_whenIncorrectUpdateRequestIsProvided_thenReturnBadRequest() {
        //Arrange
        String requestDataJson = "password:newPassword";

        // Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/update-password")
                .contentType("application/json")
                .content(requestDataJson));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("An unexpected error: JSON parse error: Unrecognized token 'password': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')"));
        verify(accountingManagementService, never()).updatePassword(any(Principal.class), any(PasswordUpdateRequest.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("addRole, correct data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddRole_whenValidUserRoleRequestIsProvided_thenReturnUserDataResponse() {
        //Arrange
        String login = VALID_USER_EMAIL_1;
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        String requestDataJson = objectMapper.writeValueAsString(USER_ROLE_REQUEST);
        String expectedDataJson = objectMapper.writeValueAsString(USER_DATA_RESPONSE);

        when(accountingManagementService.addRole(login, USER_ROLE_REQUEST)).thenReturn(USER_DATA_RESPONSE);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/assign-role/{login}", login)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, times(1)).addRole(eq(login), eq(USER_ROLE_REQUEST));
        resultActions.andExpect(status().isOk())
                .andExpect(result -> assertEquals(expectedDataJson, result.getResponse().getContentAsString()));
        assertNotEquals(login, currentLogin);
        assertTrue(USER_DATA_RESPONSE.getRoles().toString().contains(USER_ROLE_REQUEST.getRole()));
    }

    @Test
    @SneakyThrows
    @DisplayName("addRole, invalid input")
    @WithMockUser(username = VALID_USER_EMAIL_1, roles = {"ADMIN"})
    void testAddRole_whenInvalidUserRoleRequestIsProvided_thenReturnBadRequest() {
        //Arrange
        String invalidLogin = "invalid-email";
        UserRoleRequest userRoleRequest = new UserRoleRequest("SUPER_ADMIN");
        String requestDataJson = objectMapper.writeValueAsString(userRoleRequest);
        String expectedErrorMessage1 = "must be a well-formed email address";
        String expectedErrorMessage2 = UserRoleRequest.class.getDeclaredField("role")
                .getAnnotation(ValidRole.class).message();

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/assign-role/{login}", invalidLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, never()).addRole(any(String.class), any(UserRoleRequest.class));
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message", hasSize(2)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage1)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage2)));

    }

    @Test
    @SneakyThrows
    @DisplayName("addRole, invalid body")
    @WithMockUser(username = VALID_USER_EMAIL_1, roles = {"ADMIN"})
    void testAddRole_whenInvalidBodyIsProvided_thenReturnBadRequest() {
        //Arrange
        String invalidLogin = "invalid-email";
        String requestDataJson = "user:SuperAdmin";
        String expectedErrorMessage1 = "An unexpected error: JSON parse error: Unrecognized token 'user': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";

        //Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/assign-role/{login}", invalidLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, never()).addRole(any(String.class), any(UserRoleRequest.class));
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage1));

    }

    @Test
    @SneakyThrows
    @DisplayName("addRole, no permission")
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddRole_whenUserIsNotAdmin_thenReturnForbidden() {
        // Arrange
        String requestDataJson = objectMapper.writeValueAsString(USER_ROLE_REQUEST);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/api/users/assign-role/{login}", VALID_USER_EMAIL_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));

        // Assert
        resultActions.andExpect(status().isForbidden());
        verify(accountingManagementService, never()).addRole(any(String.class), any(UserRoleRequest.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("removeRole, valid input")
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void testRemoveRole_whenValidUserRoleRequestIsProvided_thenReturnUserDataResponse() {
        //Arrange
        String login = VALID_USER_EMAIL_1;
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        String requestDataJson = objectMapper.writeValueAsString(USER_ROLE_REQUEST);
        String expectedDataJson = objectMapper.writeValueAsString(USER_DATA_RESPONSE);

        when(accountingManagementService.removeRole(login, USER_ROLE_REQUEST)).thenReturn(USER_DATA_RESPONSE);

        //Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/remove-role/{login}", login)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, times(1)).removeRole(eq(login), eq(USER_ROLE_REQUEST));
        resultActions.andExpect(status().isOk())
                .andExpect(result -> assertEquals(expectedDataJson, result.getResponse().getContentAsString()));
        assertNotEquals(login, currentLogin);
        assertTrue(USER_DATA_RESPONSE.getRoles().toString().contains(USER_ROLE_REQUEST.getRole()));
    }

    @Test
    @SneakyThrows
    @DisplayName("removeRole, invalid input")
    @WithMockUser(username = VALID_USER_EMAIL_1, roles = {"ADMIN"})
    void testRemoveRole_whenInvalidUserRoleRequestIsProvided_thenReturnBadRequest() {
        //Arrange
        String invalidLogin = "invalid-email";
        UserRoleRequest userRoleRequest = new UserRoleRequest("SUPER_ADMIN");
        String requestDataJson = objectMapper.writeValueAsString(userRoleRequest);
        String expectedErrorMessage1 = "must be a well-formed email address";
        String expectedErrorMessage2 = UserRoleRequest.class.getDeclaredField("role")
                .getAnnotation(ValidRole.class).message();

        //Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/remove-role/{login}", invalidLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, never()).removeRole(any(String.class), any(UserRoleRequest.class));
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message", hasSize(2)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage1)))
                .andExpect(jsonPath("$.message", hasItem(expectedErrorMessage2)));
    }

    @Test
    @SneakyThrows
    @DisplayName("removeRole, invalid body")
    @WithMockUser(username = VALID_USER_EMAIL_1, roles = {"ADMIN"})
    void testRemoveRole_whenInvalidBodyIsProvided_thenReturnBadRequest() {
        //Arrange
        String invalidLogin = "invalid-email";
        String requestDataJson = "user:SuperAdmin";
        String expectedErrorMessage1 = "An unexpected error: JSON parse error: Unrecognized token 'user': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";

        //Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/remove-role/{login}", invalidLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDataJson));
        //Assert
        verify(accountingManagementService, never()).removeRole(any(String.class), any(UserRoleRequest.class));
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage1));

    }

    @Test
    @SneakyThrows
    @DisplayName("removeRole, no permission")
    @WithMockUser(username = "user", roles = {"USER"})
    void testRemoveRole_whenUserIsNotAdmin_thenReturnBadRequest() {
        // Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/remove-role/{login}", VALID_USER_EMAIL_1)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isBadRequest());
        verify(accountingManagementService, never()).removeRole(any(String.class), any(UserRoleRequest.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("removeUser, valid input")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRemoveUser_whenValidLoginIsProvided_thenReturnUserDataResponse() {
        // Arrange
        String validLogin = VALID_USER_EMAIL_1;
        String expectedResponseJson = objectMapper.writeValueAsString(USER_DATA_RESPONSE);

        when(accountingManagementService.removeUser(validLogin)).thenReturn(USER_DATA_RESPONSE);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/{login}", validLogin)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(result -> assertEquals(expectedResponseJson, result.getResponse().getContentAsString()));
        verify(accountingManagementService, times(1)).removeUser(validLogin);
    }

    @Test
    @SneakyThrows
    @DisplayName("removeUser, invalid email format")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRemoveUser_whenInvalidEmailIsProvided_thenReturnBadRequest() {
        // Arrange
        String invalidLogin = "invalid-email";
        String expectedErrorMessage = "must be a well-formed email address";

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/{login}", invalidLogin)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
        verify(accountingManagementService, never()).removeUser(anyString());
    }

    @Test
    @SneakyThrows
    @DisplayName("removeUser, user not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRemoveUser_whenUserDoesNotExist_thenReturnNotFound() {
        // Arrange
        String nonExistentLogin = "nonexistent@example.com";
        String exceptionMessage = "Account with login '%s' not found".formatted(nonExistentLogin);
        when(accountingManagementService.removeUser(nonExistentLogin))
                .thenThrow(new NoSuchElementException(exceptionMessage));

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/api/users/{login}", nonExistentLogin)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("An unexpected error: " + exceptionMessage))
                .andExpect(result -> assertInstanceOf(NoSuchElementException.class, result.getResolvedException()));
        verify(accountingManagementService, times(1)).removeUser(nonExistentLogin);
    }
}