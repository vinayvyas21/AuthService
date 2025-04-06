package com.vk.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.LoginResponseDto;
import com.vk.auth.dtos.RequestStatus;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.UserAlreadyExistsException;
import com.vk.auth.services.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

	UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signUp")
	public ResponseEntity<UserSignUpResponseDto> signUp(@RequestBody UserSignUpRequestDto request) {
		try {
			UserSignUpResponseDto responseDTO = this.userService.createUser(request);
			if (responseDTO.getRequestStatus() == RequestStatus.SUCCESS) {
				return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
			}
		} catch(UserAlreadyExistsException ex) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
		String token = this.userService.login(request);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("AUTH_TOKEN", token);
		LoginResponseDto response = new LoginResponseDto();
		HttpStatus status = null;
		if (token != null) {
			response.setRequestStatus(RequestStatus.SUCCESS);
			status = HttpStatus.OK;
		} else {
			response.setRequestStatus(RequestStatus.FAILURE);
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<>(response, status);
	}

}
