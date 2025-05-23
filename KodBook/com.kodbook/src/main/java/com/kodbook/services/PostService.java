package com.kodbook.services;

import java.util.List;
import java.util.Optional;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;

public interface PostService {

    void createPost(Post post);

    List<Post> getAllPosts();

    Optional<Post> getPostById(Long id);

    void updatePost(Post post);

    void deletePost(Long id, String username);

    List<Post> getPostsByUser(User user);

    void addComment(Long postId, String username, String comment);
}
