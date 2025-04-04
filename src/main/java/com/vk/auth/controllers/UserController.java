package com.vk.auth.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vk.auth.dtos.LoginRequest;
import com.vk.auth.dtos.UserSignUpRequest;
import com.vk.auth.services.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

	UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signUp")
	public void signUp(@RequestBody UserSignUpRequest request) {
		this.userService.createUser(request);
	}

	@PostMapping("/login")
	public boolean login(@RequestBody LoginRequest request) {
		return this.userService.login(request);
	}

}
