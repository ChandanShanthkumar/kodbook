package com.kodbook.controllers;

import java.io.IOException;
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
	public ResponseEntity<User> getCurrentUserProfile(HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
		}
		User user = service.getUser(username);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
		}
		return new ResponseEntity<>(user, HttpStatus.OK); // 200
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserProfileById(@PathVariable Long id) {
		User user = service.getUserById(id);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	@PutMapping("/users/me")
	public ResponseEntity<User> updateUserProfile(
		@RequestBody User userDetails,
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
		if (userDetails.getProfilePic() != null) {
            currentUser.setProfilePic(userDetails.getProfilePic());
        }

		User updatedUser = service.updateUser(currentUser);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
}
