package com.rstep1.post_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.dto.UpdatePostRequestDto;
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
            @RequestHeader("Authorization") String headerAuth) {
        
        log.info("Creating post with {}", request.toString());
        return ResponseEntity.ok(postService.createPost(request, headerAuth));
    }

    @GetMapping(value = "/posts/{id}")
    public ResponseEntity<CRUDPostResponseDto> readPost(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String headerAuth) {
        
        log.info("Reading post with id {}", id);
        return ResponseEntity.ok(postService.readPost(id, headerAuth));
    }

    @GetMapping(value = "/posts")
    public ResponseEntity<List<CRUDPostResponseDto>> readAllPostsByUser(
            @RequestParam(value = "author_id", required = false) Long authorId,
            @RequestHeader("Authorization") String headerAuth) {
        
        if (authorId != null) {
            log.info("Reading all posts by user with user_id {}", authorId);
            return ResponseEntity.ok(postService.readAllPostsByUser(authorId, headerAuth));
        }

        log.info("Reading all posts");
        return ResponseEntity.ok(postService.readAllPosts(headerAuth));
    }

    @PutMapping(value = "/posts/{id}")
    public ResponseEntity<CRUDPostResponseDto> updatePost(
            @PathVariable("id") Long id,
            @RequestBody UpdatePostRequestDto request,
            @RequestHeader("Authorization") String headerAuth) {
        
        log.info("Updating post with id {} with {}", id, request);
        return ResponseEntity.ok(postService.updatePost(id, request, headerAuth));
    }

    @DeleteMapping(value = "/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String headerAuth) {
        log.info("Deleting post with id {}", id);
        postService.deletePost(id, headerAuth);
        return ResponseEntity.noContent().build();
    }
}
