package com.rstep1.post_service.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.rstep1.common_lib.exception.ServiceException;
import com.rstep1.post_service.config.TestcontainersInitializer;
import com.rstep1.post_service.controller.PostController;
import com.rstep1.post_service.dto.CRUDPostResponseDto;
import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.exception.UnauthorizedException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class PostUserServiceIntegrationTest {

    @Autowired
    private PostController postController;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("user-service.url", wireMockServer::baseUrl);
    }

    @Test
    void whenValidToken_thenCreatePostSuccessfully() {
        stubFor(get(urlEqualTo("/auth/validate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("123")));

        CreatePostRequestDto request = new CreatePostRequestDto("Test title", "Test content");
        String validToken = "Bearer valid.token.123";

        ResponseEntity<CRUDPostResponseDto> response = postController.createPost(request, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test title", response.getBody().title());
    }

    @Test
    void whenInvalidToken_thenThrowUnauthorized() {
        stubFor(get(urlEqualTo("/auth/validate"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Invalid token\"}")));

        CreatePostRequestDto request = new CreatePostRequestDto("Test title", "Test content");
        String invalidToken = "Bearer invalid.token";

        assertThrows(UnauthorizedException.class, () -> {
            postController.createPost(request, invalidToken);
        });
    }

    @Test
    void whenUserServiceUnavailable_thenThrowServiceException() {
        stubFor(get(urlEqualTo("/auth/validate"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withFixedDelay(2000)));

        CreatePostRequestDto request = new CreatePostRequestDto("Test title", "Test content");
        String validToken = "Bearer valid.token.123";

        assertThrows(ServiceException.class, () -> {
            postController.createPost(request, validToken);
        });
    }
}
