package com.vk.auth.services;

import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.UserAlreadyExistsException;

public interface UserService {
	
	public UserSignUpResponseDto createUser(UserSignUpRequestDto request) throws UserAlreadyExistsException;
	
	public boolean updateUser();
	
	public boolean deleteUser(Long id);
	
	public String login(LoginRequestDto request);
}
