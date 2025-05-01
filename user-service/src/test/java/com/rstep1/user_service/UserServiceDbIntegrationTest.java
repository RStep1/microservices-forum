package com.rstep1.user_service;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;
import com.rstep1.user_service.dto.UserDto;
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

    private static Faker faker;

    @BeforeAll
    static void setUpAll() {
        faker = new Faker();
    }

    @Test
    public void givenUserRegistrationRequest_whenRegisterUser_thenSaveUserInDatabase() {
        final String username = faker.name().username();
        final String email = faker.internet().emailAddress();
        final String password = faker.internet().password();

        UserRegistrationRequest request = 
            UserRegistrationRequest.builder()
                .username(username)
                .email(email)
                .password(password).build();
        
        userService.registerUser(request);

        User user = userRepository.findByUsername(username).orElseThrow();

        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(email, user.getEmail(), "Email should match");
        assertNotEquals(password, user.getPassword(), "Password should be hashed");
    }

    @Test
    public void givenValidUserId_whenReadUser_thenReturnUserDto() {
        final User testUser = new User();
        final String username = faker.name().username();
        final String email = faker.internet().emailAddress();
        testUser.setUsername(username);
        testUser.setEmail(email);
        testUser.setPassword(faker.internet().password());

        User savedUser = userRepository.save(testUser);
        final Long id = savedUser.getId();

        UserDto foundUserDto = userService.readUser(id);

        assertEquals(id, foundUserDto.id(), "Returned DTO ID should be saved user ID");
        assertEquals(username, foundUserDto.username(), "Username should match");
        assertEquals(email, foundUserDto.email(), "Email should match");
    }
}
