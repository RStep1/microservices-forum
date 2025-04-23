package com.rstep1.post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rstep1.post_service.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}
