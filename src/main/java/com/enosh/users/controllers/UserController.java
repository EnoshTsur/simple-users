package com.enosh.users.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enosh.users.model.User;
import com.enosh.users.repository.UserRepository;
import com.enosh.users.response.Response;
import com.sun.el.stream.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private final UserRepository userRepository;

	private final String USER_KEY = "user";
	private final String NOT_LOGGED_IN_MESSAGE = "Must be loggedin in order to continue";		
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	private boolean isLoggedIn(HttpServletRequest request) {
		return request.getSession().getAttribute(USER_KEY) != null;
	}
	
	@GetMapping("/getAll")
	public ResponseEntity<Response<List<User>>> getAll(HttpServletRequest request) {
		if (!isLoggedIn(request)) {
			return ResponseEntity.ok(
						new Response<>(false, NOT_LOGGED_IN_MESSAGE, null)
					);
		}
		List<User> all = new ArrayList<>();
		userRepository.findAll().forEach(all::add);
		
		return ResponseEntity.ok(
					new Response<>(true, null, all)
				);
		
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<Response<User>> register(@RequestBody User user, HttpServletRequest request) {
		String username = user.getUsername();
		
		return userRepository.findByUsername(username)
				.map(byName -> ResponseEntity.ok(
						new Response<>(
								false, 
								"User by the username: " + username + " already exists", 
								user
						)
				)).orElseGet(() -> {
					
					User afterSave = userRepository.save(user);
					request.getSession().setAttribute(USER_KEY, afterSave);
					
					return ResponseEntity.ok(
						new Response<>(true, null, afterSave)	
					);
				});
					
	}
	
	
	
	@PostMapping("/login")
	public ResponseEntity<Response<User>> login(@RequestBody User user, HttpServletRequest request) {
		
		String username = user.getUsername();
		String password = user.getPassword();
		
		return userRepository.findByUsernameAndPassword(username, password)
				.map(userById ->  {
					
						request.getSession().setAttribute(USER_KEY, userById);
						return ResponseEntity.ok(
								new Response<>(true, null, userById)
						);
				})
				.orElseGet(() -> ResponseEntity.ok(
							new Response<>(false, "Wrong username or password", user)
				));
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
						new Response<>(false, NOT_LOGGED_IN_MESSAGE, null)
				);
	}
}

