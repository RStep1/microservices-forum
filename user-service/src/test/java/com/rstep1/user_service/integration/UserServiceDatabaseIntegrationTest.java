package com.rstep1.user_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.user_service.config.TestcontainersInitializer;
import com.rstep1.user_service.dto.UpdateUserProfileRequest;
import com.rstep1.user_service.dto.UserDto;
import com.rstep1.user_service.exception.EmailExistsException;
import com.rstep1.user_service.model.User;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.service.UserService;
import com.rstep1.user_service.util.TestUserUtils;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class UserServiceDatabaseIntegrationTest  {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
    public void whenReadAllUsers_thenReturnCorrectNumberOfUsers() {
        User testUser1 = TestUserUtils.createTestUser();
        User testUser2 = TestUserUtils.createTestUser();
        User testUser3 = TestUserUtils.createTestUser();
        List<User> testUsers = Arrays.asList(testUser1, testUser2, testUser3);

        userRepository.saveAll(testUsers);

        List<UserDto> foundUsersDto = userService.readUsers();
        
        assertEquals(testUsers.size(), foundUsersDto.size());
    }

    @Test
    public void givenValidUpdateRequest_whenUpdateUserProfile_thenUpdateUserInDatabase() {
        User existingUser = userRepository.save(TestUserUtils.createTestUser());
        String newEmail = "new.email@example.com";
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest(newEmail);

        UserDto updatedUser = userService.updateUserProfile(existingUser.getId(), updateRequest);

        assertEquals(newEmail, updatedUser.email(), "Email should be updated");

        User dbUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals(newEmail, dbUser.getEmail(), "Database should reflect the email update");
    }

    @Test
    public void givenDuplicateEmail_whenUpdateUserProfile_thenThrowEmailExistsException() {
        User user1 = userRepository.save(TestUserUtils.createTestUser());
        User user2 = userRepository.save(TestUserUtils.createTestUser());
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest(user2.getEmail());

        assertThrows(EmailExistsException.class, () -> {
            userService.updateUserProfile(user1.getId(), updateRequest);
        });
    }

    @Test
    public void givenNonExistingUserId_whenUpdateUserProfile_thenThrowEntityNotFoundException() {
        Long nonExistingId = 999L;
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest("new.email@example.com");

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserProfile(nonExistingId, updateRequest);
        });
    }

    @Test
    public void givenExistingUserId_whenDeleteUser_thenRemoveUserFromDatabase() {
        User user = userRepository.save(TestUserUtils.createTestUser());

        userService.deleteUser(user.getId());

        assertFalse(userRepository.existsById(user.getId()), "User should be deleted from database");
    }

    @Test
    public void givenNonExistingUserId_whenDeleteUser_thenThrowEntityNotFoundException() {
        Long nonExistingId = 999L;

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(nonExistingId);
        });
    }
}
