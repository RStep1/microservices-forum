package com.rstep1.user_service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.google.common.base.Objects;

import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.service.UserService;
import com.rstep1.user_service.model.User;

@SpringBootTest
@Transactional
public class UserServiceDbIntegrationTest extends AbstractDbIntegrationTest {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenUserRegistrationRequest_whenRegisterUser_thenSaveUserInDatabase() {
        final String username = "username";
        final String email = "email";
        final String password = "passord";

        UserRegistrationRequest request = 
            UserRegistrationRequest.builder()
                .username(username)
                .email(email)
                .password(password).build();
        
        userService.registerUser(request);

        User user = userRepository.findByUsername(username).orElseThrow();
        assertTrue(user.getId() != null &&
                Objects.equal(username, user.getUsername()) &&
                Objects.equal(email, user.getEmail()));
    }

    @Test
    public void givenValidUserId_whenReadUser_thenReturnUser() {
        
    }
}
