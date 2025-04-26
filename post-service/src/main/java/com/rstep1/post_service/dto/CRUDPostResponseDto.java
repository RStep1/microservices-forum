package com.rstep1.post_service.dto;

import java.time.LocalDateTime;

import com.rstep1.post_service.model.Post;

public record CRUDPostResponseDto(
    Long id,
    // String authorUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String title,
    String content,
    Long voteCount
    ) {
    
    public static CRUDPostResponseDto from(Post post) {
        return new CRUDPostResponseDto(
            post.getId(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            post.getTitle(), 
            post.getContent(),
            post.getVoteCount()
        );
    }
}