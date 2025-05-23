// CommentRepository.java
package com.kodbook.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodbook.entities.Comment;
import com.kodbook.entities.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
