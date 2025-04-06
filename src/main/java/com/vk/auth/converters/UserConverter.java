package com.vk.auth.converters;

import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.models.User;

public class UserConverter {

	public static User convertUserSignUpRequestToUser(UserSignUpRequestDto request) {

		User user = new User();

		user.setEmail(request.getEmail());
		user.setGender(request.getGender());

		return user;
	}

}
