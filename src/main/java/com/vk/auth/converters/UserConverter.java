package com.vk.auth.converters;

import com.vk.auth.dtos.UserSignUpRequest;
import com.vk.auth.models.User;

public class UserConverter {

	public static User convertUserSignUpRequestToUser(UserSignUpRequest request) {

		User user = new User();

		user.setEmail(request.getEmail());
		user.setGender(request.getGender());

		return user;
	}

}
