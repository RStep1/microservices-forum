package com.rstep1.post_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.rstep1.post_service.clients.UserServiceClient;
import com.rstep1.post_service.dto.CRUDPostResponseDto;
import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.model.Post;
import com.rstep1.post_service.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    
    public CRUDPostResponseDto createPost(CreatePostRequestDto request, String token) {

        Long authorId = userServiceClient.validateToken(token).getBody();

        Post post = new Post();
        post.setAuthorId(authorId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(null);
        post.setTitle(request.title());
        post.setContent(request.content());

        return CRUDPostResponseDto.from(postRepository.save(post));
    }

    public CRUDPostResponseDto readPost(Long id) {

        return null;
    }
}
