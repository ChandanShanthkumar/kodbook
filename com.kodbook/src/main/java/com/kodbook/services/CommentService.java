package com.kodbook.services;

import com.kodbook.entities.Post;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public Object createComment(Long postId, Post comment) {
        return null;
    }

    public void deleteComment(Long postId, Long commentId) {
    }
}