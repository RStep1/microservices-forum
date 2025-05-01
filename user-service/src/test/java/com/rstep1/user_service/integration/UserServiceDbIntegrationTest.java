package com.rstep1.user_service.integration;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.user_service.dto.UserDto;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.service.UserService;
import com.rstep1.user_service.util.TestUserUtils;
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
        UserRegistrationRequest request = TestUserUtils.createRegistrationRequest();
        
        userService.registerUser(request);

        User user = userRepository.findByUsername(request.username()).orElseThrow();

        assertEquals(request.username(), user.getUsername(), "Username should match");
        assertEquals(request.email(), user.getEmail(), "Email should match");
        assertNotEquals(request.password(), user.getPassword(), "Password should be hashed");
    }

    @Test
    public void givenValidUserId_whenReadUser_thenReturnUserDto() {
        User testUser = TestUserUtils.createTestUser();

        User savedUser = userRepository.save(testUser);
        Long id = savedUser.getId();

        UserDto foundUserDto = userService.readUser(id);

        assertEquals(id, foundUserDto.id(), "Returned DTO ID should be saved user ID");
        assertEquals(testUser.getUsername(), foundUserDto.username(), "Username should match");
        assertEquals(testUser.getEmail(), foundUserDto.email(), "Email should match");
    }

    @Test
    public void whenReadAllUsers_thenReturnUserList() {
        
    }
}
