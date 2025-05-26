package com.kodbook.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 30, message = "Some message")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password; // Should be stored hashed (e.g., BCrypt)

    private LocalDate dob;

//    @Enumerated(EnumType.STRING)
    private String gender;

    private String city;
    private String bio;
    private String college;
    private String linkedIn;
    private String gitHub;

    private String profilePicUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(mappedBy = "likedByUsers", fetch = FetchType.LAZY)
    private Set<Post> likedPosts = new HashSet<>();

    public User(String bio, String city, String college, LocalDate dob, String email, Gender gender, String gitHub, Long id, String linkedIn, String password, String profilePicUrl, String username) {
        this.bio = bio;
        this.city = city;
        this.college = college;
        this.dob = dob;
        this.email = email;
        this.gender = gender.name();
        this.gitHub = gitHub;
        this.id = id;
        this.linkedIn = linkedIn;
        this.password = password;
        this.profilePicUrl = profilePicUrl;
        this.username = username;
    }

    // --- Enums ---
    public enum Role {
        USER, ADMIN
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // --- Constructors ---
    public User() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getDob() { return dob; }

    /**
     * Setter that accepts a String and converts it to LocalDate.
     * If the format is invalid, dob is set to null.
     * Expected format: yyyy-MM-dd
     */
    public void setDob(String dobString) {
        if (dobString == null || dobString.isBlank()) {
            this.dob = null;
            return;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.dob = LocalDate.parse(dobString, formatter);
        } catch (DateTimeParseException e) {
            this.dob = null; // Or handle error as needed
        }
    }

    /**
     * Overloaded setter to accept LocalDate directly if needed.
     */
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() { return gender; }

    /**
     * Setter that accepts a String and converts it to Gender enum.
     * Converts input to uppercase and matches enum constants.
     * If invalid, sets gender to null.
     */
    public void setGender(String genderString) {
        if (genderString == null || genderString.isBlank()) {
            this.gender = null;
            return;
        }
        try {
            this.gender = genderString;
        } catch (IllegalArgumentException e) {
            this.gender = null; // Or set a default value or throw exception
        }
    }

    /**
     * Overloaded setter to accept Gender directly if needed.
     */
    public void setGender(Gender gender) {
        this.gender = gender.name();
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getLinkedIn() { return linkedIn; }
    public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }

    public String getGitHub() { return gitHub; }
    public void setGitHub(String gitHub) { this.gitHub = gitHub; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public Set<Post> getLikedPosts() { return likedPosts; }
    public void setLikedPosts(Set<Post> likedPosts) { this.likedPosts = likedPosts; }

    // --- equals and hashCode (based on id) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString (exclude password) ---
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob +
                ", gender=" + gender +
                ", city='" + city + '\'' +
                ", bio='" + bio + '\'' +
                ", college='" + college + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", gitHub='" + gitHub + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", role=" + role +
                '}';
    }
}
