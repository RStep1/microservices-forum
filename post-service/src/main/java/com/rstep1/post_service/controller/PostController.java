package com.rstep1.post_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.dto.CRUDPostResponseDto;
import com.rstep1.post_service.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/post-service")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;

    @PostMapping(value = "/posts")
    public ResponseEntity<CRUDPostResponseDto> createPost(
            @RequestBody CreatePostRequestDto request,
            @RequestHeader("Authorization") String token) {
        
        log.info("Creating post with {}", request.toString());
        return ResponseEntity.ok(postService.createPost(request, token));
    }

    @GetMapping(value = "/posts/{id}")
    public ResponseEntity<CRUDPostResponseDto> readPost(@PathVariable("id") Long id) {
        log.info("Reading post with id {}", id);
        return ResponseEntity.ok(postService.readPost(id));
    }

    // @GetMapping(value = "posts/user_id")

    // @PutMapping(value = "posts/{id}")
}
