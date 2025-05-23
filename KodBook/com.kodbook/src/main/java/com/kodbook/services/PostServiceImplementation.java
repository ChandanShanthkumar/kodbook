package com.kodbook.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;
import com.kodbook.repositories.PostRepository;

@Service
public class PostServiceImplementation implements PostService {

    private final PostRepository repo;

    public PostServiceImplementation(PostRepository repo) {
        this.repo = repo;
    }

    @Override
    public void createPost(Post post) {
        repo.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return repo.findAll();
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void updatePost(Post post) {
        repo.save(post);
    }

    @Override
    public void deletePost(Long id, String username) {
        // TODO: Add logic to check if the post belongs to the user before deleting for security.
        // For now, simply deletes by id.
        repo.deleteById(id);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return repo.findByUser(user);
    }

    @Override
    public void addComment(Long postId, String username, String comment) {
        // TODO: Implement comment logic
        throw new UnsupportedOperationException("addComment is not implemented yet.");
    }
}
