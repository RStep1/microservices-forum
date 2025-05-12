package com.rstep1.user_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.user_service.config.TestcontainersInitializer;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.exception.IncorrectEmailException;
import com.rstep1.user_service.exception.IncorrectUsernameException;
import com.rstep1.user_service.model.User;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.service.AuthService;
import com.rstep1.user_service.util.TestUserUtils;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class AuthServiceRegisterDbIntegrationTest {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenValidRegistrationRequest_whenRegisterUser_thenSaveUserInDatabase() {
        UserRegistrationRequest request = TestUserUtils.createRandomRegistrationRequest();
        
        authService.registerUser(request);

        User user = userRepository.findByUsername(request.username()).orElseThrow();

        assertEquals(request.username(), user.getUsername(), "Username should match");
        assertEquals(request.email(), user.getEmail(), "Email should match");
        assertNotEquals(request.password(), user.getPassword(), "Password should be hashed");
    }

    @Test
    public void givenDuplicateUsername_whenRegisterUser_thenThrowUserAlreadyExistsException() {
        UserRegistrationRequest request = TestUserUtils.createRandomRegistrationRequest();
        authService.registerUser(request);

        UserRegistrationRequest duplicateRequest = new UserRegistrationRequest(
            request.username(),
            "different@email.com",
            "differentPassword"
        );

        assertThrows(IncorrectUsernameException.class, () -> {
            authService.registerUser(duplicateRequest);
        }, "Should throw IncorrectUsernameException for duplicate username");
    }

    @Test
    public void givenDuplicateEmail_whenRegisterUser_thenThrowUserAlreadyExistsException() {
        UserRegistrationRequest request = TestUserUtils.createRandomRegistrationRequest();
        authService.registerUser(request);

        UserRegistrationRequest duplicateRequest = new UserRegistrationRequest(
            "differentUsername",
            request.email(),
            "differentPassword"
        );

        assertThrows(IncorrectEmailException.class, () -> {
            authService.registerUser(duplicateRequest);
        }, "Should throw IncorrectEmailException for duplicate email");
    }
}