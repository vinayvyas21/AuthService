package com.vk.auth.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.LoginResponseDto;
import com.vk.auth.dtos.RequestStatus;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.InvalidTokenException;
import com.vk.auth.exceptions.UserAlreadyExistsException;
import com.vk.auth.exceptions.UserNotFoundException;
import com.vk.auth.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Test
	void signUpReturnsCreatedWhenUserIsSuccessfullyCreated() {
		UserSignUpRequestDto request = new UserSignUpRequestDto();
		UserSignUpResponseDto response = new UserSignUpResponseDto();
		response.setRequestStatus(RequestStatus.SUCCESS);

		when(userService.createUser(request)).thenReturn(response);

		ResponseEntity<UserSignUpResponseDto> result = userController.signUp(request);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals(RequestStatus.SUCCESS, result.getBody().getRequestStatus());
	}

	@Test
	void signUpReturnsConflictWhenUserAlreadyExists() {
		UserSignUpRequestDto request = new UserSignUpRequestDto();
		when(userService.createUser(request)).thenThrow(UserAlreadyExistsException.class);

		ResponseEntity<UserSignUpResponseDto> result = userController.signUp(request);

		assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
		assertEquals(RequestStatus.FAILURE, result.getBody().getRequestStatus());
	}

	@Test
	void loginReturnsOkWhenLoginIsSuccessful() {
		LoginRequestDto request = new LoginRequestDto();
		String token = "validToken";
		when(userService.login(request)).thenReturn(token);

		ResponseEntity<LoginResponseDto> result = userController.login(request);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(RequestStatus.SUCCESS, result.getBody().getRequestStatus());
		assertTrue(result.getHeaders().containsKey("AUTH_TOKEN"));
	}

	@Test
	void loginReturnsNotFoundWhenUserIsNotFound() {
		LoginRequestDto request = new LoginRequestDto();
		when(userService.login(request)).thenThrow(UserNotFoundException.class);

		ResponseEntity<LoginResponseDto> result = userController.login(request);

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		assertEquals(RequestStatus.FAILURE, result.getBody().getRequestStatus());
	}

	@Test
	void validateTokenReturnsOkWhenTokenIsValid() {
		String token = "validToken";
		when(userService.validate(token)).thenReturn(true);

		ResponseEntity<Boolean> result = userController.validateToken(token);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertTrue(result.getBody());
	}

	@Test
	void validateTokenReturnsUnauthorizedWhenTokenIsInvalid() {
		String token = "invalidToken";
		when(userService.validate(token)).thenReturn(false);

		ResponseEntity<Boolean> result = userController.validateToken(token);

		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
		assertFalse(result.getBody());
	}

	@Test
	void logoutReturnsOkWhenLogoutIsSuccessful() {
		String token = "validToken";
		when(userService.logout(token)).thenReturn(true);

		ResponseEntity<String> result = userController.logout(token);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals("Logout is successful", result.getBody());
	}

	@Test
	void logoutReturnsUnauthorizedWhenTokenIsInvalid() {
		String token = "invalidToken";
		when(userService.logout(token)).thenThrow(InvalidTokenException.class);

		ResponseEntity<String> result = userController.logout(token);

		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
		assertEquals("Invalid Token", result.getBody());
	}

	@Test
	void logoutAllReturnsOkWhenLogoutFromAllDevicesIsSuccessful() {
		Long userId = 1L;

		ResponseEntity<String> result = userController.logout(userId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals("Logout is successful from all devices", result.getBody());
	}

	@Test
	void logoutAllReturnsNotFoundWhenUserIdIsInvalid() {
		Long userId = 1L;
		doThrow(UserNotFoundException.class).when(userService).logoutAll(userId);

		ResponseEntity<String> result = userController.logout(userId);

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		assertEquals("Invalid User Id", result.getBody());
	}
}
