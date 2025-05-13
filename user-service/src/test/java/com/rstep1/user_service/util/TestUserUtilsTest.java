package com.rstep1.user_service.util;

import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestUserUtilsTest {

    @Test
    void whenCreateTestUser_thenReturnUserWithAllFieldsSet() {
        User user = TestUserUtils.createRandomTestUser();
        
        assertNotNull(user);
        assertNotNull(user.getUsername());
        assertNotNull(user.getEmail());
        assertNotNull(user.getPassword());
    }

    @Test
    void whenCreateRegistrationRequest_thenReturnRequestWithAllFieldsSet() {
        UserRegistrationRequest request = TestUserUtils.createRandomRegistrationRequest();
        
        assertNotNull(request);
        assertNotNull(request.username());
        assertNotNull(request.email());
        assertNotNull(request.password());
    }

    @Test
    void whenCreateTestUser_thenGenerateDifferentValuesEachTime() {
        User user1 = TestUserUtils.createRandomTestUser();
        User user2 = TestUserUtils.createRandomTestUser();
        
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getEmail(), user2.getEmail());
        assertNotEquals(user1.getPassword(), user2.getPassword());
    }

    @Test
    void whenRegistrationRequest_thenGenerateDifferentValuesEachTime() {
        UserRegistrationRequest request1 = TestUserUtils.createRandomRegistrationRequest();
        UserRegistrationRequest request2 = TestUserUtils.createRandomRegistrationRequest();
        
        assertNotEquals(request1.username(), request2.username());
        assertNotEquals(request1.email(), request2.email());
        assertNotEquals(request1.password(), request2.password());
    }
}