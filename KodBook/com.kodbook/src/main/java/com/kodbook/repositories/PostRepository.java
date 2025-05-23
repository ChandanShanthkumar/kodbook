package com.kodbook.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
}
