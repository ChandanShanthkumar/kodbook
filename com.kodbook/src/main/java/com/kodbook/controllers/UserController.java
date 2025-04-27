package com.kodbook.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.kodbook.entities.Post;
import com.kodbook.entities.Comment;
import com.kodbook.entities.User;
import com.kodbook.services.PostService;
import com.kodbook.services.UserService;

import jakarta.servlet.http.HttpSession;


@RestController
public class UserController {
	@Autowired
	UserService service;
	@Autowired
	PostService postService;
	@PostMapping("/signUp")
	public String addUser(@ModelAttribute User user) {
		//user exists?
		String username = user.getUsername();
		String email = user.getEmail();
		boolean status = service.userExists(username, email);
		if(status == false) {
			service.addUser(user);
		}
		return "index";
	} 
	
	@PostMapping("/login")
	public String login(@RequestParam String username,
			@RequestParam String password,
			Model model, HttpSession session)	{
		boolean status = service.validateUser(username, password);
		if(status == true) {
			List<Post> allPosts = postService.fetchAllPosts();
			
			session.setAttribute("username", username);
			model.addAttribute("session", session);
			
			model.addAttribute("allPosts", allPosts);
			
			return "home";
		}
		else {
			return "login failed";
		}
	}


	@GetMapping("/users/me")	
	public String getCurrentUserProfile(HttpSession session, Model model) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return "redirect:/";
		}
		User user = service.getUser(username);
		if (user == null) {
			return "redirect:/"; 
		}
		model.addAttribute("user", user);
		return "myProfile";
	}

	@GetMapping("/users/{id}")
	public String getUserProfileById(@PathVariable Long id, Model model) {
		User user = service.getUserById(id);
		if (user == null) {
			return "redirect:/";
		}
		model.addAttribute("user", user);
		return "showUserProfile";
	}
	@PutMapping("/users/me")
	public String updateUserProfile( @RequestParam("photo") MultipartFile file, @ModelAttribute User userDetails,
		HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		User currentUser = service.getUser(username);
		if (currentUser == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		currentUser.setDob(userDetails.getDob());
		currentUser.setGender(userDetails.getGender());
		currentUser.setCity(userDetails.getCity());
		currentUser.setBio(userDetails.getBio());
		currentUser.setCollege(userDetails.getCollege());
		currentUser.setLinkedIn(userDetails.getLinkedIn());
		currentUser.setGitHub(userDetails.getGitHub());
		 if (!file.isEmpty()) {
	            try {
	                byte[] bytes = file.getBytes();
	                String base64Encoded = Base64.getEncoder().encodeToString(bytes);
	                currentUser.setPhotoBase64(base64Encoded);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		
		User updatedUser = service.updateUser(currentUser);
		return "redirect:/users/me";
	}
	
	
	@PutMapping("/users/me/changepassword")
	public String changePassword(@RequestParam String password,
			HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return "redirect:/";
		}
		User currentUser = service.getUser(username);
		if (currentUser == null) {
			return "redirect:/";
		}
		currentUser.setPassword(password);
		
		User updatedUser = service.updateUser(currentUser);
		
		return "redirect:/users/me";
		
	}
	
	
	@GetMapping("/search/users")
	public String searchUsers(@RequestParam String username, Model model) {
		
		List<User> users = service.searchUserByUsername(username);
		model.addAttribute("users", users);
		return "searchUsers";
		
	}
}
