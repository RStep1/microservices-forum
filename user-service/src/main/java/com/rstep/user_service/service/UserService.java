package com.rstep.user_service.service;

// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rstep.user_service.dto.UserRegistryDto;
import com.rstep.user_service.exception.IncorrectEmailException;
import com.rstep.user_service.exception.IncorrectUsernameException;
import com.rstep.user_service.model.User;
import com.rstep.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    public UserRegistryDto registerUser(UserRegistryDto userRegistryDto) {

        if (userRepository.existsByUsername(userRegistryDto.username())) {
            log.error("Trying to register user with existing username");
            throw new IncorrectUsernameException("This username already registered, please try again.");
        }

        if (userRepository.existsByEmail(userRegistryDto.email())) {
            log.error("Trying to register user with existing email");
            throw new IncorrectEmailException("This email already registered, please try again.");
        }

        User user = new User();
        user.setUsername(userRegistryDto.username());
        user.setEmail(userRegistryDto.email());
        // user.setPassword(passwordEncoder.encode(userRegistryDto.password()));
        user.setPassword(userRegistryDto.password());

        return UserRegistryDto.from(userRepository.save(user));
    }
}
