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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private PostService postService;

    @PostMapping("/signUp")
    public String addUser(@ModelAttribute User user, Model model) {
        String username = user.getUsername();
        String email = user.getEmail();

        if (service.userExists(username, email)) {
            model.addAttribute("error", "Username or Email already exists!");
            return "signUp";
        }

        service.addUser(user);
        model.addAttribute("success", "Registration successful! Please log in.");
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model, HttpSession session) {
        if (service.validateUser(username, password)) {
            session.setAttribute("username", username);
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("allPosts", allPosts);
            return "home";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "index";
        }
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String dob,
                                @RequestParam String gender,
                                @RequestParam String city,
                                @RequestParam String bio,
                                @RequestParam String college,
                                @RequestParam String linkedIn,
                                @RequestParam String gitHub,
                                @RequestParam(required = false) MultipartFile profilePic,
                                HttpSession session,
                                Model model) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        Optional<User> userOpt = service.getUser(username);
if (userOpt.isEmpty()) {
    // Optionally, add an error message
    model.addAttribute("error", "User not found. Please log in again.");
    return "index"; // or redirect to login
}
User user = userOpt.get();


        user.setDob(dob);
        user.setGender(gender);
        user.setCity(city);
        user.setBio(bio);
        user.setCollege(college);
        user.setLinkedIn(linkedIn);
        user.setGitHub(gitHub);

        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                // Save the file to a local directory (e.g., src/main/resources/static/uploads)
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

        service.updateUser(user);
        model.addAttribute("user", user);
        model.addAttribute("success", "Profile updated successfully!");
        return "myProfile";
    }
}
