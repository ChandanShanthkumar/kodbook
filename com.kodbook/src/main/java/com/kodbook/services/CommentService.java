package com.kodbook.services;

import com.kodbook.entities.Post;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

    public Post createComment(Long postId, Post comment) {
        
    }

    public void deleteComment(Long postId, Long commentId) {
    }
}