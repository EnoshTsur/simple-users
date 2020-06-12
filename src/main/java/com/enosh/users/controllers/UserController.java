package com.enosh.users.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enosh.users.model.User;
import com.enosh.users.repository.UserRepository;
import com.enosh.users.response.Response;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserRepository userRepository;

	private final String USER_KEY = "user";
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	private boolean isLoggedIn(HttpServletRequest request) {
		return request.getSession().getAttribute(USER_KEY) != null;
	}
	
	// http://localhost:8080/user/get/1
	
	@GetMapping("/get/{id}")
	public ResponseEntity<Response<User>> byId(@PathVariable("id") Long id, HttpServletRequest request) {
		return isLoggedIn(request) ? userRepository
				.findById(id)
				.map(user -> ResponseEntity.ok(
						new Response<>(true,null,user)
				))
				.orElse(ResponseEntity.ok(
						new Response<>(false, "No user by the id: " + id, null)
				
				)) : ResponseEntity.ok(
						new Response<>(false, "Must be loggedin in order to continue", null)
				);
	}
}

