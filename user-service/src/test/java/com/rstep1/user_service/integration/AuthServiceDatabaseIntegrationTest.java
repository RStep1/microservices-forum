package com.rstep1.user_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.user_service.config.TestcontainersInitializer;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.model.User;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.service.AuthService;
import com.rstep1.user_service.util.TestUserUtils;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class AuthServiceDatabaseIntegrationTest {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Disabled
    @Test
    public void givenUserRegistrationRequest_whenRegisterUser_thenSaveUserInDatabase() {
        UserRegistrationRequest request = TestUserUtils.createRegistrationRequest();
        
        authService.registerUser(request);

        User user = userRepository.findByUsername(request.username()).orElseThrow();

        assertEquals(request.username(), user.getUsername(), "Username should match");
        assertEquals(request.email(), user.getEmail(), "Email should match");
        assertNotEquals(request.password(), user.getPassword(), "Password should be hashed");
    }
}
