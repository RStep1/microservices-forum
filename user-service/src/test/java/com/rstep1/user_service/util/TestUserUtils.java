package com.rstep1.user_service.util;

import com.github.javafaker.Faker;
import com.rstep1.user_service.dto.auth.UserCredentialDto;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.model.User;

public class TestUserUtils {
    private static final Faker faker = new Faker();
    
    public static User createTestUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public static User createRandomTestUser() {
        return createTestUser(
            faker.name().username(),
            faker.internet().emailAddress(),
            faker.internet().password()
        );
    }

    public static UserRegistrationRequest createRegistrationRequest(
        String username, 
        String email, 
        String password
    ) {
        return UserRegistrationRequest.builder()
            .username(username)
            .email(email)
            .password(password)
            .build();
    }

    public static UserRegistrationRequest createRandomRegistrationRequest() {
        return createRegistrationRequest(
            faker.name().username(),
            faker.internet().emailAddress(),
            faker.internet().password()
        );
    }

    public static UserCredentialDto createUserCredentialDto(String username, String password) {
        return UserCredentialDto.builder()
            .username(username)
            .password(password)
            .build();
    }

    public static UserCredentialDto createRandomUserCredentialDto() {
        return createUserCredentialDto(
            faker.name().username(),
            faker.internet().password()
        );
    }
}