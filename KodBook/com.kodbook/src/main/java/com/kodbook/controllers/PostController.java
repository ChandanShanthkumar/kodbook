package com.kodbook.controllers;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // Create a new post
    @PostMapping("/createPost")
    public String createPost(
            @RequestParam("caption") String caption,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            return "redirect:/";
        }

        try {
            Post post = new Post();
            post.setCaption(caption);
            post.setUser(user);

            // If you want to handle the photo as Base64:
            if (photo != null && !photo.isEmpty()) {
                post.setPhotoBase64(java.util.Base64.getEncoder().encodeToString(photo.getBytes()));
            }

            postService.createPost(post);
            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create post: " + e.getMessage());
        }
        return "redirect:/goHome";
    }

    // Like a post
    @PostMapping("/likePost")
    public String likePost(@RequestParam("id") Long postId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        try {
            postService.addComment(postId, username, ""); // If you have a likePost method, use it instead
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not like post.");
        }
        return "redirect:/goHome";
    }

    // Add a comment to a post
    @PostMapping("/addComment")
    public String addComment(@RequestParam("id") Long postId,
                             @RequestParam("comment") String comment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        if (comment == null || comment.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Comment cannot be empty");
            return "redirect:/goHome";
        }

        try {
            postService.addComment(postId, username, comment);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not add comment.");
        }
        return "redirect:/goHome";
    }

    // Delete a post
    @PostMapping("/deletePost")
    public String deletePost(@RequestParam("id") Long postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        try {
            postService.deletePost(postId, username);
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not delete post: " + e.getMessage());
        }
        return "redirect:/openMyProfile";
    }
}
