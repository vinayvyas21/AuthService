package com.vk.auth.services;

import com.vk.auth.dtos.LoginRequest;
import com.vk.auth.dtos.UserSignUpRequest;

public interface UserService {
	
	public void createUser(UserSignUpRequest request);
	
	public boolean updateUser();
	
	public boolean deleteUser(Long id);
	
	public boolean login(LoginRequest request);
}
