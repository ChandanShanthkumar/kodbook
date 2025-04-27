package com.kodbook.services;

import com.kodbook.entities.Post;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImplementation implements CommentService {

    @Override
    public Post createComment(Long postId, Post post) {
        
        return null;
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
       
    }
}