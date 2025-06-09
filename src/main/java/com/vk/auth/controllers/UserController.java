package com.vk.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.LoginResponseDto;
import com.vk.auth.dtos.RequestStatus;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.ActiveSessionsLimitationException;
import com.vk.auth.exceptions.InvalidTokenException;
import com.vk.auth.exceptions.UserAlreadyExistsException;
import com.vk.auth.exceptions.UserNotFoundException;
import com.vk.auth.exceptions.WrongPasswordException;
import com.vk.auth.services.UserService;

/**
 * UserController handles user authentication and management operations.
 * It provides endpoints for user sign-up, login, token validation, and logout.
 */
@RestController
@RequestMapping("/auth")
public class UserController {

	UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signUp")
	public ResponseEntity<UserSignUpResponseDto> signUp(@RequestBody UserSignUpRequestDto request) {
		UserSignUpResponseDto response = new UserSignUpResponseDto();
		try {
			response = this.userService.createUser(request);
			if (response.getRequestStatus() == RequestStatus.SUCCESS) {
				return new ResponseEntity<>(response, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (UserAlreadyExistsException ex) {
			response.setRequestStatus(RequestStatus.FAILURE);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
		HttpStatus status = null;
		LoginResponseDto response = new LoginResponseDto();
		try {
			String token = this.userService.login(request);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("AUTH_TOKEN", token);
			if (token != null) {
				response.setRequestStatus(RequestStatus.SUCCESS);
				status = HttpStatus.OK;
				return new ResponseEntity<>(response, headers, status);
			} else {
				response.setRequestStatus(RequestStatus.FAILURE);
				status = HttpStatus.UNAUTHORIZED;
			}
		} catch (UserNotFoundException ex) {
			response.setRequestStatus(RequestStatus.FAILURE);
			status = HttpStatus.NOT_FOUND;
		} catch (WrongPasswordException ex) {
			response.setRequestStatus(RequestStatus.FAILURE);
			status = HttpStatus.BAD_REQUEST;
		} catch(ActiveSessionsLimitationException ex) {
			response.setRequestStatus(RequestStatus.FAILURE);
			status = HttpStatus.CONFLICT;
		}
		return new ResponseEntity<>(response, status);
	}

	@GetMapping("/validate")
	public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
		boolean isValidToken = this.userService.validate(token);
		if (!isValidToken) {
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam("token") String token) {
		try {
			boolean isLogoutSuccessful = userService.logout(token);
			if (isLogoutSuccessful) {
				return new ResponseEntity<>("Logout is successful", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Logout is not successful", HttpStatus.BAD_REQUEST);
			}
		} catch (InvalidTokenException ex) {
			return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
		}

	}

	@GetMapping("/logoutAll/{userId}")
	public ResponseEntity<String> logout(@PathVariable("userId") Long userId) {
		try {
			userService.logoutAll(userId);
			return new ResponseEntity<>("Logout is successful from all devices", HttpStatus.OK);
		} catch (UserNotFoundException ex) {
			return new ResponseEntity<>("Invalid User Id", HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			return new ResponseEntity<>("Getting unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
