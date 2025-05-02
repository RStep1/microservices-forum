package com.rstep1.user_service.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.rstep1.common_lib.exception.ServiceException;
import com.rstep1.user_service.dto.UpdateUserProfileRequest;
import com.rstep1.user_service.dto.UserDto;
import com.rstep1.user_service.exception.DataConflictException;
import com.rstep1.user_service.exception.EmailExistsException;
import com.rstep1.user_service.exception.UserDeletionException;
import com.rstep1.user_service.model.User;
import com.rstep1.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto readUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return UserDto.from(user);
    }

    public List<UserDto> readUsers() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    public UserDto updateUserProfile(Long id, UpdateUserProfileRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with id: {}", id);
                return new EntityNotFoundException("User not found with id: " + id);
            });
        
        if (!user.getEmail().equals(request.email())) {
            if (userRepository.existsByEmail(request.email())) {
                log.error("Duplicate email detected: {}", request.email());
                throw new EmailExistsException("Email '" + request.email() + "' is already in use");
            }
        }
        
        try {
            user.setEmail(request.email());
            return UserDto.from(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating user {}: {}", id, e.getMessage());
            throw new DataConflictException("Failed to update user due to data conflict", e);
        } catch (Exception e) {
            log.error("Unexpected error updating user {}: {}", id, e.getMessage());
            throw new ServiceException("Failed to update user profile", e);
        }
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        try {
            userRepository.deleteById(id);
            log.info("User with id {} was successfully deleted", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while deleting user {}: {}", id, e.getMessage());
            throw new UserDeletionException("Cannot delete user due to existing references");
        } catch (JpaSystemException | PersistenceException e) {
            log.error("Unexpected error deleting user {}: {}", id, e.getMessage());
            throw new ServiceException("Failed to delete user due to persistence error", e);
        }
    }


}
