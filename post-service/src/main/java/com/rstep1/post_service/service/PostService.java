package com.rstep1.post_service.service;

import java.time.LocalDateTime;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rstep1.common_lib.dto.ErrorResponse;
import com.rstep1.post_service.clients.UserServiceClient;
import com.rstep1.post_service.dto.CRUDPostResponseDto;
import com.rstep1.post_service.dto.CreatePostRequestDto;
import com.rstep1.post_service.exception.UnauthorizedException;
import com.rstep1.post_service.model.Post;
import com.rstep1.post_service.repository.PostRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    
    public CRUDPostResponseDto createPost(CreatePostRequestDto request, String token) {
        try {
            ResponseEntity<?> response = userServiceClient.validateToken(token);

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

    public CRUDPostResponseDto readPost(Long id) {

        return null;
    }
}
