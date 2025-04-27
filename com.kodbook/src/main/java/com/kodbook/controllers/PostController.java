package com.kodbook.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kodbook.entities.Post;
import com.kodbook.entities.User;
import com.kodbook.exceptions.PostNotFoundException;
import com.kodbook.services.PostService;
import com.kodbook.services.PostServiceImplementation;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	@Autowired
	PostService service;
	@Autowired
	UserService userService;

	// Create a new post
	@PostMapping
	public ResponseEntity<Post> createPost(@RequestParam("caption") String caption,
			@RequestParam("photo") MultipartFile photo, HttpSession session) {

		String username = (String) session.getAttribute("username");
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Or handle as needed
		}

		User user = userService.getUser(username);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Post post = new Post();
		// updating post object
		post.setUser(user);
		post.setCaption(caption);
		try {
			post.setPhoto(photo.getBytes());
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Post createdPost = service.createPost(post);
		return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
	}

	// Get all posts
	@GetMapping
	public List<Post> getAllPosts() {
		return service.fetchAllPosts();
	}

	// Get a post by ID
	@GetMapping("/{id}")
	public ResponseEntity<Post> getPost(@PathVariable Long id) {
		Optional<Post> post = Optional.ofNullable(service.getPost(id));
		return post.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Show the form to edit a post
	@GetMapping("/edit/{id}")
	public String showEditPostForm(@PathVariable Long id, Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return "redirect:/login"; // Redirect to login if not authenticated
		}
	
		Post existingPost = service.getPost(id);
		if (existingPost == null) {
			throw new PostNotFoundException("Post not found with id: " + id);
		}
		if (!existingPost.getUser().getUsername().equals(username)) {
			return "redirect:/"; // Redirect if user doesn't own the post
		}
	
		model.addAttribute("post", existingPost);
		return "editPost";
	}

	// Update a post
	@PostMapping("/edit/{id}")
	public String updatePost(@PathVariable Long id, @RequestParam String caption, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return "redirect:/login"; // Redirect to login if not authenticated
		}

		Post post = service.getPost(id);
		if (!post.getUser().getUsername().equals(username)) {
			return "redirect:/"; // Redirect if user doesn't own the post
		}
		
		post.setCaption(caption);
		service.updatePost(post);
		return "redirect:/";
	}

	// Delete a post
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Long id) {
		service.deletePost(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/likePost")
	public String likePost(@RequestParam Long id, Model model) {
		Post post= service.getPost(id);
		post.setLikes(post.getLikes() + 1);
		service.updatePost(post);
		
		List<Post> allPosts = service.fetchAllPosts();
		model.addAttribute("allPosts", allPosts);
		return "home";
	}

	@PostMapping("/addComment")
	public String addComment(@RequestParam Long id, 
			@RequestParam String comment, Model model) {
		System.out.println(comment);
		Post post= service.getPost(id);
		List<String> comments = post.getComments();
		if(comments == null) {
			comments = new ArrayList<String>();
		}
		comments.add(comment);
		post.setComments(comments);
		service.updatePost(post);
		
		List<Post> allPosts = service.fetchAllPosts();
		model.addAttribute("allPosts", allPosts);
		return "home";
	}
}
