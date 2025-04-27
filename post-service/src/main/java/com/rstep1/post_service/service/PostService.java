package com.rstep1.post_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rstep1.post_service.clients.UserServiceClient;
import com.rstep1.post_service.dto.CRUDPostResponseDto;
import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.dto.UpdatePostRequestDto;
import com.rstep1.post_service.exception.NotPostOwnerException;
import com.rstep1.post_service.exception.UnauthorizedException;
import com.rstep1.post_service.model.Post;
import com.rstep1.post_service.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    
    public CRUDPostResponseDto createPost(CreatePostRequestDto request, String headerAuth) {
        try {
            ResponseEntity<?> response = userServiceClient.validateToken(headerAuth);

            Long authorId = (Long) response.getBody();
                
            Post post = new Post();
            post.setAuthorId(authorId);
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(null);
            post.setTitle(request.title());
            post.setContent(request.content());
            
            return CRUDPostResponseDto.from(postRepository.save(post));
        } catch (UnauthorizedException e) {
            log.warn("UnauthorizedException exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service communication failed: {}", e.getMessage());
            throw new ServiceException("Service unavailable", e);
        }
    }

    public CRUDPostResponseDto readPost(Long id, String headerAuth) {
        userServiceClient.validateToken(headerAuth);
        Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return CRUDPostResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public List<CRUDPostResponseDto> readAllPostsByUser(Long authorId, String headerAuth) {
        userServiceClient.validateToken(headerAuth);
        return postRepository.findByAuthorId(authorId).stream().map(CRUDPostResponseDto::from).toList();
    }

    public List<CRUDPostResponseDto> readAllPosts(String headerAuth) {
        userServiceClient.validateToken(headerAuth);
        return postRepository.findAll().stream().map(CRUDPostResponseDto::from).toList();
    }

    public CRUDPostResponseDto updatePost(Long id, UpdatePostRequestDto request, String headerAuth) {
        ResponseEntity<?> response = userServiceClient.validateToken(headerAuth);
        Long authorId = (Long) response.getBody();

        Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (post.getAuthorId() != authorId) {
            log.info("Client is trying to update not his own post");
            throw new NotPostOwnerException("Only the owner of the post can update it.");
        }
        
        post.setContent(request.content());
        
        return CRUDPostResponseDto.from(postRepository.save(post));
    }

    public void deletePost(Long id, String headerAuth) {
        ResponseEntity<?> response = userServiceClient.validateToken(headerAuth);
        Long authorId = (Long) response.getBody();

        Post post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (post.getAuthorId() != authorId) {
            log.info("Client is trying to delete not his own post");
            throw new NotPostOwnerException("Only the owner of the post can delete it.");
        }

        postRepository.delete(post);
    }
}
