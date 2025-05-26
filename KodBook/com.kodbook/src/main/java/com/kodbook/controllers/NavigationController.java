package com.kodbook.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import javax.servlet.Servlet;

@Controller
@RequestMapping("/")
public class NavigationController {

    private final UserService userService;
    private final PostService postService;

    public NavigationController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    // Landing page or redirect to home if logged in
    @GetMapping
    public String index(@SessionAttribute(name = "username", required = false) String username) {
        return (username != null) ? "redirect:/home" : "index";
    }

    // Signup page or redirect if logged in
    @GetMapping("signup")
    public String openSignUp(@SessionAttribute(name = "username", required = false) String username) {
        return (username != null) ? "redirect:/home" : "signUp";
    }

    // Create post page; only accessible if logged in
    @GetMapping("create-post")
    public String openCreatePost(@SessionAttribute(name = "username", required = false) String username, RedirectAttributes redirectAttributes) {
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/";
        }
        return "createPost";
    }

    // Home page with posts and current user data
    @GetMapping("home")
    public String home(Model model, @SessionAttribute(name = "username", required = false) String username, RedirectAttributes redirectAttributes) {
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access home.");
            return "redirect:/";
        }

        userService.getUser(username).ifPresentOrElse(
            user -> model.addAttribute("currentUser", user),
            () -> {
                // If user not found in DB, force logout or redirect
                redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
            }
        );

        model.addAttribute("allPosts", postService.getAllPosts());
        return "home";
    }

    // User's own profile page
    @GetMapping("profile")
    public String myProfile(Model model,
                            @SessionAttribute(name = "username", required = false) String username,
                            RedirectAttributes redirectAttributes) {
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to view profile.");
            return "redirect:/";
        }

        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/home";
        }

        model.addAttribute("user", user);
        model.addAttribute("myPosts", user.getPosts());
        return "myProfile";
    }

    // Visiting another user's profile by username
    @GetMapping("profile/{username}")
    public String visitProfile(@PathVariable String username, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        model.addAttribute("myPosts", user.getPosts());
        return "showUserProfile";
    }

    // Edit profile page; only accessible if logged in
    @GetMapping("edit-profile")
    public String openEditProfile(@SessionAttribute(name = "username", required = false) String username,
                                  RedirectAttributes redirectAttributes) {
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to edit your profile.");
            return "redirect:/";
        }
        return "editProfile";
    }

    // Logout and invalidate session
    @PostMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout";
    }
}
