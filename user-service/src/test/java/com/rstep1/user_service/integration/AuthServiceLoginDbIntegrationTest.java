package com.rstep1.user_service.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.user_service.config.TestcontainersInitializer;
import com.rstep1.user_service.dto.auth.UserCredentialDto;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.exception.AuthenticationFailedException;
import com.rstep1.user_service.service.AuthService;
import com.rstep1.user_service.util.TestUserUtils;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class AuthServiceLoginDbIntegrationTest {
    
    @Autowired
    private AuthService authService;

    private static UserRegistrationRequest registeredUser;

    @BeforeAll
    public static void setup(@Autowired AuthService authService) {
        registeredUser = TestUserUtils.createRandomRegistrationRequest();
    }

    @Test
    public void givenValidCredentials_whenLogin_thenReturnAuthenticationToken() {
        authService.registerUser(registeredUser);
        UserCredentialDto loginRequest = new UserCredentialDto(
            registeredUser.username(),
            registeredUser.password()
        );

        String token = authService.verify(loginRequest);

        assertNotNull(token, "Authentication token should not be null");
        assertFalse(token.isEmpty(), "Authentication token should not be empty");
    }

    @Test
    public void givenInvalidPassword_whenLogin_thenThrowAuthenticationFailedException() {
        authService.registerUser(registeredUser);
        UserCredentialDto invalidLoginRequest = new UserCredentialDto(
            registeredUser.username(),
            "wrongPassword"
        );

        assertThrows(AuthenticationFailedException.class, () -> {
            authService.verify(invalidLoginRequest);
        }, "Should throw AuthenticationFailedException for invalid password");
    }

    @Test
    public void givenNonExistentUsername_whenLogin_thenThrowAuthenticationFailedException() {
        authService.registerUser(registeredUser);
        UserCredentialDto nonExistentUserRequest = new UserCredentialDto(
            "nonexistentuser",
            "anypassword"
        );

        assertThrows(AuthenticationFailedException.class, () -> {
            authService.verify(nonExistentUserRequest);
        }, "Should throw AuthenticationFailedException for non-existent user");
    }

    @Test
    public void givenEmptyUsername_whenLogin_thenThrowAuthenticationFailedException() {
        authService.registerUser(registeredUser);
        UserCredentialDto emptyUsernameRequest = new UserCredentialDto(
            "",
            "anypassword"
        );

        assertThrows(AuthenticationFailedException.class, () -> {
            authService.verify(emptyUsernameRequest);
        }, "Should throw AuthenticationFailedException for empty username");
    }

    @Test
    public void givenEmptyPassword_whenLogin_thenThrowAuthenticationFailedException() {
        authService.registerUser(registeredUser);
        UserCredentialDto emptyPasswordRequest = new UserCredentialDto(
            registeredUser.username(),
            ""
        );

        assertThrows(AuthenticationFailedException.class, () -> {
            authService.verify(emptyPasswordRequest);
        }, "Should throw AuthenticationFailedException for empty password");
    }
}