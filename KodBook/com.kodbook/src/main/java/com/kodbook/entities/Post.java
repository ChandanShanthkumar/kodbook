package com.kodbook.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caption;

    // Store image URL (not the image itself)
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // Track likes by users
    @ManyToMany
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    // Comments will be a separate entity, mapped here
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<User> getLikedByUsers() { return likedByUsers; }
    public void setLikedByUsers(Set<User> likedByUsers) { this.likedByUsers = likedByUsers; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Utility method for likes count
    public int getLikesCount() { return likedByUsers.size(); }

    @Override
    public String toString() {
        return "Post [id=" + id + ", caption=" + caption + ", photoUrl=" + photoUrl +
               ", user=" + (user != null ? user.getUsername() : null) +
               ", likes=" + getLikesCount() +
               ", createdAt=" + createdAt + "]";
    }

    public void setPhotoBase64(String encodeToString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
