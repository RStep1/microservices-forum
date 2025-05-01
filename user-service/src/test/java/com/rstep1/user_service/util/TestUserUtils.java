package com.rstep1.user_service.util;

import com.github.javafaker.Faker;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.model.User;

public class TestUserUtils {

    private static final Faker faker = new Faker();

    public static User createTestUser() {
        User user = new User();
        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        return user;
    }

    public static UserRegistrationRequest createRegistrationRequest() {
        return UserRegistrationRequest.builder()
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .build();
    }
}