package com.kodbook.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NavigationController {
    
    private final UserService userService;
    private final PostService postService;

    public NavigationController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        return session.getAttribute("username") != null ? "redirect:/home" : "index";
    }

    @GetMapping("/signup")
    public String openSignUp(HttpSession session) {
        return session.getAttribute("username") != null ? "redirect:/home" : "signUp";
    }

    @GetMapping("/create-post")
    public String openCreatePost(HttpSession session) {
        return session.getAttribute("username") != null ? "createPost" : "redirect:/";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/";
        
        model.addAttribute("allPosts", postService.getAllPosts());

        model.addAttribute("currentUser", userService.getUser(username));
        return "home";
    }

    @GetMapping("/profile")
    public String myProfile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if(username == null) return "redirect:/";
        
        User user = userService.getUser(username).orElse(null);
		if(user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        model.addAttribute("myPosts", user.getPosts());
        return "myProfile";
    }

    @GetMapping("/profile/{username}")
    public String visitProfile(@PathVariable String username, Model model, RedirectAttributes redirectAttributes) {
	User user = userService.getUser(username).orElse(null);
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        model.addAttribute("myPosts", user.getPosts());
        return "showUserProfile";
    }

    @GetMapping("/edit-profile")
    public String openEditProfile(HttpSession session) {
        return session.getAttribute("username") != null ? "editProfile" : "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout";
    }
}
