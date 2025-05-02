package com.rstep1.user_service.integration;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.rstep1.user_service.service.UserService;

@WebMvcTest(UserService.class)
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;
}
