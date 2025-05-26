package com.kodbook.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    // Show login page or redirect if already logged in
    @GetMapping("/login")
    public String showLoginForm(@SessionAttribute(name = "username", required = false) String username) {
        return (username != null) ? "redirect:/home" : "index"; // index.html is login page
    }

    // Handle login form submission
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        if (userService.validateUser(username, password)) {
            session.setAttribute("username", username);
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("allPosts", allPosts);
            return "home";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "index";
        }
    }

    // Show signup page or redirect if already logged in
    @GetMapping("/signUp")
    public String showSignUpForm(@SessionAttribute(name = "username", required = false) String username) {
        return (username != null) ? "redirect:/home" : "signUp";
    }

    // Handle signup form submission
    @PostMapping("/signUp")
    public String addUser(@ModelAttribute User user, Model model) {
        String username = user.getUsername();
        String email = user.getEmail();

        if (userService.userExists(username, email)) {
            model.addAttribute("error", "Username or Email already exists!");
            return "signUp";
        }

        userService.addUser(user);
        model.addAttribute("success", "Registration successful! Please log in.");
        return "index";
    }

    // Show edit profile page only if logged in
    @GetMapping("/editProfile")
    public String showEditProfile(@SessionAttribute(name = "username", required = false) String username,
                                  Model model) {
        if (username == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUser(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found. Please log in again.");
            return "index";
        }

        model.addAttribute("user", userOpt.get());
        return "editProfile";
    }

    // Handle profile update form submission
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String dob,
                                @RequestParam String gender,
                                @RequestParam String city,
                                @RequestParam String bio,
                                @RequestParam String college,
                                @RequestParam String linkedIn,
                                @RequestParam String gitHub,
                                @RequestParam(required = false) MultipartFile profilePic,
                                @SessionAttribute(name = "username", required = false) String username,
                                Model model) {

        if (username == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUser(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found. Please log in again.");
            return "index";
        }

        User user = userOpt.get();

        // Update user fields
        user.setDob(dob);
        user.setGender(gender);
        user.setCity(city);
        user.setBio(bio);
        user.setCollege(college);
        user.setLinkedIn(linkedIn);
        user.setGitHub(gitHub);

        // Handle profile picture upload if present
        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                String fileName = System.currentTimeMillis() + "_" + profilePic.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, profilePic.getBytes());

                // Set the URL relative to your static folder
                user.setProfilePicUrl("/uploads/" + fileName);

            } catch (IOException e) {
                model.addAttribute("error", "Failed to upload profile picture.");
                return "editProfile";
            }
        }

        userService.updateUser(user);

        model.addAttribute("user", user);
        model.addAttribute("success", "Profile updated successfully!");
        return "myProfile";
    }
}
